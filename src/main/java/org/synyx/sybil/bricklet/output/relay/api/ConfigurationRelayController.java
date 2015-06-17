package org.synyx.sybil.bricklet.output.relay.api;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import org.synyx.sybil.api.PatchResource;
import org.synyx.sybil.api.SinglePatchResource;
import org.synyx.sybil.bricklet.output.relay.EnumRelay;
import org.synyx.sybil.bricklet.output.relay.Relay;
import org.synyx.sybil.bricklet.output.relay.RelayService;
import org.synyx.sybil.bricklet.output.relay.database.RelayDomain;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

//TODO: Add 404 for non-existing Relays


/**
 * ConfigurationRelayController.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@RestController
@RequestMapping("/configuration/relays")
public class ConfigurationRelayController {

    private RelayService relayService;

    @Autowired
    public ConfigurationRelayController(RelayService relayService) {

        this.relayService = relayService;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = { "application/hal+json" })
    public Resources<RelayResource> relays() {

        List<RelayDomain> relays = relayService.getAllDomains();
        List<RelayResource> resources = new ArrayList<>();
        List<Link> links = new ArrayList<>();

        Link self = linkTo(ConfigurationRelayController.class).withSelfRel();
        links.add(self);

        for (RelayDomain relayDomain : relays) {
            RelayResource resource = getRelay(relayDomain.getName());

            resources.add(resource);
        }

        return new Resources<>(resources, links);
    }


    @ResponseBody
    @RequestMapping(method = RequestMethod.PATCH, produces = { "application/hal+json" })
    public Resources<RelayResource> updateRelays(@RequestBody PatchResource input) throws Exception {

        List<RelayDomain> relays = relayService.getAllDomains();
        List<RelayResource> resources = new ArrayList<>();
        List<Link> links = new ArrayList<>();

        Link self = linkTo(ConfigurationRelayController.class).withSelfRel();
        links.add(self);

        for (RelayDomain relayDomain : relays) {
            for (SinglePatchResource patch : input.getPatches()) {
                if (patch.getAction().equals("set") && patch.getTarget().equals("relays")) {
                    Relay relay = relayService.getRelay(relayDomain);
                    boolean state = patch.getValues().get(0).equals("true");
                    relay.setStates(state, state);
                } else {
                    throw new Exception("Unknown action or target.");
                }
            }

            RelayResource resource = getRelay(relayDomain.getName());
            resources.add(resource);
        }

        return new Resources<>(resources, links);
    }


    @ResponseBody
    @RequestMapping(value = "/{name}", method = RequestMethod.GET, produces = { "application/hal+json" })
    public RelayResource getRelay(@PathVariable String name) {

        RelayDomain relayDomain = relayService.getDomain(name);
        Relay relay = relayService.getRelay(relayDomain);

        List<Link> links = new ArrayList<>();

        links.add(linkTo(methodOn(ConfigurationRelayController.class).getRelay(relayDomain.getName())).withSelfRel());

        RelayResource result = new RelayResource(relayDomain, links);

        result.setRelays(relay.getState(EnumRelay.ONE), relay.getState(EnumRelay.TWO));

        return result;
    }


    @ResponseBody
    @RequestMapping(value = "/{name}", method = RequestMethod.PATCH, produces = { "application/hal+json" })
    public RelayResource updateRelay(@PathVariable String name, @RequestBody PatchResource input) throws Exception {

        RelayDomain relayDomain = relayService.getDomain(name);
        Relay relay = relayService.getRelay(relayDomain);

        for (SinglePatchResource patch : input.getPatches()) {
            switch (patch.getAction()) {
                case "set":
                    switch (patch.getTarget()) {
                        case "relay1":
                            relay.setState(EnumRelay.ONE, patch.getValues().get(0).equals("true"));
                            break;

                        case "relay2":
                            relay.setState(EnumRelay.TWO, patch.getValues().get(0).equals("true"));
                            break;

                        case "relays":
                            relay.setStates(patch.getValues().get(0).equals("true"),
                                patch.getValues().get(1).equals("true"));
                            break;

                        default:
                            throw new Exception("Unknown target for action set");
                    }

                    break;

                case "toggle":
                    switch (patch.getTarget()) {
                        case "relay1":
                            relay.setState(EnumRelay.ONE, !relay.getState(EnumRelay.ONE));
                            break;

                        case "relay2":
                            relay.setState(EnumRelay.TWO, !relay.getState(EnumRelay.TWO));
                            break;

                        case "relays":
                            relay.setStates(!relay.getState(EnumRelay.ONE), !relay.getState(EnumRelay.TWO));
                            break;

                        default:
                            throw new Exception("Unknown target for action toggle");
                    }

                    break;

                default:
                    throw new Exception("Unknown action");
            }
        }

        return getRelay(relayDomain.getName());
    }


    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> errorHandler(Exception e) {

        String error = "Error parsing input: " + e.toString();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
