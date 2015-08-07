package org.synyx.sybil.relay.dto;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.synyx.sybil.relay.service.RelayService;


/**
 * RelayDTOService.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Service
public class RelayDTOService {

    private final RelayService relayService;

    @Autowired
    public RelayDTOService(RelayService relayService) {

        this.relayService = relayService;
    }

    public RelayDTO get(String name) {

        return new RelayDTO(relayService.get(name));
    }


    public RelayDTO turnOn(String name) {

        return new RelayDTO(relayService.enable(name));
    }


    public RelayDTO turnOff(String name) {

        return new RelayDTO(relayService.disable(name));
    }


    public RelayDTO toggle(String name) {

        return new RelayDTO(relayService.toggle(name));
    }
}
