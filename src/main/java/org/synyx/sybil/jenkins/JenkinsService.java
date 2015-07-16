package org.synyx.sybil.jenkins;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import org.apache.commons.codec.binary.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Profile;

import org.springframework.core.env.Environment;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.stereotype.Service;

import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import org.synyx.sybil.AttributeEmptyException;
import org.synyx.sybil.LoadFailedException;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStripDTOService;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStripService;
import org.synyx.sybil.bricklet.output.ledstrip.domain.LEDStripDTO;
import org.synyx.sybil.jenkins.domain.ConfiguredJob;
import org.synyx.sybil.jenkins.domain.ConfiguredServer;
import org.synyx.sybil.jenkins.domain.JenkinsJob;
import org.synyx.sybil.jenkins.domain.JenkinsProperties;
import org.synyx.sybil.jenkins.domain.Status;
import org.synyx.sybil.jenkins.domain.StatusInformation;

import java.io.File;
import java.io.IOException;

import java.nio.charset.Charset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PreDestroy;


/**
 * JenkinsService.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Service
public class JenkinsService {

    private static final Logger LOG = LoggerFactory.getLogger(JenkinsService.class);
    private static final long ONE_MINUTE_IN_MILLISECONDS = 60000;
    private static final int DELAY_DIVISOR = 4;

    private final ObjectMapper objectMapper;
    private final LEDStripService ledStripService;
    private final LEDStripDTOService ledStripDTOService;
    private final String configDirectory;
    private final String jenkinsServerConfigFile;
    private final RestTemplate restTemplate;

    @Autowired
    public JenkinsService(ObjectMapper objectMapper, LEDStripService ledStripService,
        LEDStripDTOService ledStripDTOService, Environment environment, RestTemplate restTemplate) {

        this.objectMapper = objectMapper;
        this.ledStripService = ledStripService;
        this.ledStripDTOService = ledStripDTOService;
        this.restTemplate = restTemplate;
        configDirectory = environment.getProperty("path.to.configfiles");
        jenkinsServerConfigFile = environment.getProperty("jenkins.configfile");
    }

    @PreDestroy
    public void turnOffAllLEDStrips() {

        try {
            List<LEDStripDTO> ledstrips = ledStripDTOService.getAllDTOs();

            for (LEDStripDTO ledStripDTO : ledstrips) {
                ledStripService.turnOff(ledStripDTO);
            }
        } catch (IOException | TimeoutException | NotConnectedException | AlreadyConnectedException
                | AttributeEmptyException | LoadFailedException exception) {
            LOG.error("Error turning off LED strips:", exception);
        }
    }


    @Profile("default")
    @Scheduled(initialDelay = ONE_MINUTE_IN_MILLISECONDS / DELAY_DIVISOR, fixedRate = ONE_MINUTE_IN_MILLISECONDS)
    public void runEveryMinute() {

        Map<String, HttpEntity<JenkinsProperties[]>> authorizations;
        Map<String, List<ConfiguredJob>> configuredJobs;

        try {
            authorizations = loadAuthorizations();
            configuredJobs = loadConfiguredJobs();
        } catch (IOException exception) {
            LOG.error("Error loading Jenkins configuration:", exception);

            return;
        }

        List<String> servers = new ArrayList<>(authorizations.keySet());
        List<JenkinsJob> jobs;
        Map<String, StatusInformation> ledStripStatuses = new HashMap<>();

        for (String server : servers) {
            try {
                jobs = getJobsFromJenkins(server, authorizations.get(server));
                ledStripStatuses = getLEDStripStatusesFromJobs(jobs, configuredJobs.get(server), ledStripStatuses);
            } catch (RestClientException exception) {
                LOG.error("Error retrieving jobs from Jenkins:", exception);
            }
        }

        applyStatuses(ledStripStatuses);
    }


    private Map<String, HttpEntity<JenkinsProperties[]>> loadAuthorizations() throws IOException {

        Map<String, HttpEntity<JenkinsProperties[]>> authorizations = new HashMap<>();

        List<ConfiguredServer> configuredServers = objectMapper.readValue(new File(jenkinsServerConfigFile),
                new TypeReference<List<ConfiguredServer>>() {
                });

        for (ConfiguredServer configuredServer : configuredServers) {
            authorizations.put(configuredServer.getUrl(), generateHTTPHeader(configuredServer));
        }

        return authorizations;
    }


    private HttpEntity<JenkinsProperties[]> generateHTTPHeader(ConfiguredServer server) {

        HttpHeaders headers = new HttpHeaders();

        headers.set("Authorization",
            "Basic "
            + new String(
                Base64.encodeBase64((server.getUser() + ":" + server.getKey()).getBytes(Charset.forName("US-ASCII")))));

        return new HttpEntity<>(headers);
    }


    private Map<String, List<ConfiguredJob>> loadConfiguredJobs() throws IOException {

        return objectMapper.readValue(new File(configDirectory + "jenkins.json"),
                new TypeReference<Map<String, List<ConfiguredJob>>>() {
                });
    }


    private List<JenkinsJob> getJobsFromJenkins(String server, HttpEntity<JenkinsProperties[]> authorization) {

        ResponseEntity<JenkinsProperties> response = restTemplate.exchange(server + "/api/json", HttpMethod.GET,
                authorization, JenkinsProperties.class);

        return Arrays.asList(response.getBody().getJobs());
    }


    private Map<String, StatusInformation> getLEDStripStatusesFromJobs(List<JenkinsJob> jobs,
        List<ConfiguredJob> configuredJobs, Map<String, StatusInformation> ledStripStatuses) {

        if (configuredJobs == null) {
            return ledStripStatuses;
        }

        for (JenkinsJob job : jobs) {
            StatusInformation jobStatus = getStatusFromJob(job);
            List<String> ledStrips = getLedStripFromConfiguredJob(job, configuredJobs);

            if (ledStrips.isEmpty()) {
                continue;
            }

            for (String ledStrip : ledStrips) {
                ledStripStatuses.put(ledStrip, higherStatus(jobStatus, ledStripStatuses.get(ledStrip)));
            }
        }

        return ledStripStatuses;
    }


    private StatusInformation getStatusFromJob(JenkinsJob job) {

        StatusInformation statusInformation;

        switch (job.getColor()) {
            case "red":
            case "red_anime":
                statusInformation = new StatusInformation(job.getName(), Status.CRITICAL);
                break;

            case "yellow":
            case "yellow_anime":
                statusInformation = new StatusInformation(job.getName(), Status.WARNING);
                break;

            default:
                statusInformation = new StatusInformation(job.getName(), Status.OKAY);
                break;
        }

        return statusInformation;
    }


    private List<String> getLedStripFromConfiguredJob(JenkinsJob job, List<ConfiguredJob> configuredJobs) {

        List<String> result = new ArrayList<>();

        for (ConfiguredJob configuredJob : configuredJobs) {
            if (configuredJob.getName().equals(job.getName())) {
                result.add(configuredJob.getLedstrip());
            }
        }

        return result;
    }


    private StatusInformation higherStatus(StatusInformation newStatus, StatusInformation currentStatus) {

        if (currentStatus == null || newStatus.getStatus().ordinal() > currentStatus.getStatus().ordinal()) {
            return newStatus;
        } else {
            return currentStatus;
        }
    }


    private void applyStatuses(Map<String, StatusInformation> ledStripStatuses) {

        for (String ledStrip : ledStripStatuses.keySet()) {
            try {
                LEDStripDTO ledStripDTO = ledStripDTOService.getDTO(ledStrip);
                ledStripDTO.setStatus(ledStripStatuses.get(ledStrip));
                ledStripService.handleStatus(ledStripDTO);
            } catch (TimeoutException | NotConnectedException | IOException | AlreadyConnectedException
                    | AttributeEmptyException | LoadFailedException exception) {
                LOG.error("Error setting status on LED strip:", exception);
            }
        }
    }
}
