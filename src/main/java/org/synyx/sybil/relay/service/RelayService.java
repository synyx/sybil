package org.synyx.sybil.relay.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Service;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import org.springframework.web.client.RestTemplate;

import org.synyx.sybil.relay.persistence.PwrControl;
import org.synyx.sybil.relay.persistence.PwrControlRepository;
import org.synyx.sybil.relay.persistence.Relay;
import org.synyx.sybil.relay.persistence.RelayRepository;


/**
 * RelayService.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Service
public class RelayService {

    private static final String REQUEST_STATUS_URL = "/strg.cfg";
    private static final String REQUEST_SET_URL = "/ctrl.htm";
    private static final String HTTP = "http://";
    private static final String RELAY_PREFIX = "F";
    private static final String RELAY_TOGGLE = "T";
    private static final String ERROR_NOT_CONFIGURED = " is not configured.";
    private static final String RELAY = "Relay ";
    private static final int EIGHT = 8;
    private static final int TWENTY = 20;

    private final RelayRepository relayRepository;
    private final PwrControlRepository pwrControlRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public RelayService(PwrControlRepository pwrControlRepository, RelayRepository relayRepository,
        RestTemplate restTemplate) {

        this.restTemplate = restTemplate;
        this.pwrControlRepository = pwrControlRepository;
        this.relayRepository = relayRepository;
    }

    public boolean get(String name) {

        Relay relay = relayRepository.get(name);

        if (relay == null) {
            throw new RelayNotFoundException(RELAY + name + ERROR_NOT_CONFIGURED);
        }

        return getState(relay);
    }


    private boolean getState(Relay relay) {

        return getStates(relay.getHost())[relay.getNumber()];
    }


    private boolean[] getStates(String host) {

        PwrControl pwrControl = pwrControlRepository.get(host);

        if (pwrControl == null) {
            throw new RelayNotFoundException("NET PwrCtrl " + host + " not found");
        }

        HttpEntity httpEntity = new HttpEntity(pwrControl.getHeader());

        ResponseEntity<String> response = restTemplate.exchange(HTTP + pwrControl.getHost() + REQUEST_STATUS_URL,
                HttpMethod.GET, httpEntity, String.class);

        boolean[] result = parseResponse(response.getBody());

        if (result.length == 0) {
            throw new RelayConnectionException("Error getting relay status from host" + host);
        }

        return result;
    }


    private boolean[] parseResponse(String response) {

        if (!response.endsWith("end;NET - Power Control")) {
            return new boolean[0];
        }

        boolean[] result = new boolean[EIGHT];

        String[] parts = response.split(";");

        for (int index = 0; index < EIGHT; index++) {
            result[index] = ("1".equals(parts[index + TWENTY]));
        }

        return result;
    }


    public boolean toggle(String name) {

        Relay relay = relayRepository.get(name);

        if (relay == null) {
            throw new RelayNotFoundException(RELAY + name + ERROR_NOT_CONFIGURED);
        }

        toggleRelay(relay);

        return getState(relay);
    }


    private void toggleRelay(Relay relay) {

        String host = relay.getHost();
        int number = relay.getNumber();

        PwrControl pwrControl = pwrControlRepository.get(host);

        if (pwrControl == null) {
            throw new RelayNotFoundException("NET PwrCtrl " + host + ERROR_NOT_CONFIGURED);
        }

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add(RELAY_PREFIX + String.valueOf(number), RELAY_TOGGLE);

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(formData, pwrControl.getHeader());

        ResponseEntity<String> response = restTemplate.exchange(HTTP + pwrControl.getHost() + REQUEST_SET_URL,
                HttpMethod.POST, httpEntity, String.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RelayConnectionException("Error setting relay " + relay.getName());
        }
    }


    public boolean enable(String name) {

        Relay relay = relayRepository.get(name);

        if (relay == null) {
            throw new RelayNotFoundException(RELAY + name + ERROR_NOT_CONFIGURED);
        }

        if (!getState(relay)) {
            toggleRelay(relay);
        }

        return true;
    }


    public boolean disable(String name) {

        Relay relay = relayRepository.get(name);

        if (relay == null) {
            throw new RelayNotFoundException(RELAY + name + ERROR_NOT_CONFIGURED);
        }

        if (getState(relay)) {
            toggleRelay(relay);
        }

        return false;
    }
}
