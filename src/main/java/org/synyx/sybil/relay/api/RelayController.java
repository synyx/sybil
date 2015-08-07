package org.synyx.sybil.relay.api;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import org.synyx.sybil.APIError;
import org.synyx.sybil.LoadFailedException;
import org.synyx.sybil.relay.dto.RelayDTO;
import org.synyx.sybil.relay.dto.RelayDTOService;
import org.synyx.sybil.relay.service.RelayConnectionException;
import org.synyx.sybil.relay.service.RelayNotFoundException;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;


/**
 * RelayController.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@RestController
@RequestMapping("/configuration/relays/{name}")
public class RelayController {

    private final RelayDTOService relayDTOService;

    @Autowired
    public RelayController(RelayDTOService relayDTOService) {

        this.relayDTOService = relayDTOService;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = { "application/json" })
    public RelayDTO get(@PathVariable String name) {

        return relayDTOService.get(name);
    }


    @ResponseBody
    @RequestMapping(method = RequestMethod.PUT, produces = "application/json")
    public RelayDTO put(@PathVariable String name) {

        return relayDTOService.toggle(name);
    }


    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public RelayDTO post(@PathVariable String name) {

        return relayDTOService.turnOn(name);
    }


    @ResponseBody
    @RequestMapping(method = RequestMethod.DELETE, produces = "application/json")
    public RelayDTO delete(@PathVariable String name) {

        return relayDTOService.turnOff(name);
    }


    @ExceptionHandler({ RelayNotFoundException.class })
    public ResponseEntity<APIError> notFoundError(Exception exception) {

        return new ResponseEntity<>(new APIError(exception.getMessage()), NOT_FOUND);
    }


    @ExceptionHandler({ RelayConnectionException.class, LoadFailedException.class })
    public ResponseEntity<APIError> serverError(Exception exception) {

        return new ResponseEntity<>(new APIError(exception.getMessage()), INTERNAL_SERVER_ERROR);
    }
}
