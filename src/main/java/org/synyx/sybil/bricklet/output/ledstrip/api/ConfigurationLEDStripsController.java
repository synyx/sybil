package org.synyx.sybil.bricklet.output.ledstrip.api;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import org.synyx.sybil.LoadFailedException;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStripDTOService;
import org.synyx.sybil.bricklet.output.ledstrip.domain.LEDStripDTO;
import org.synyx.sybil.bricklet.output.ledstrip.domain.LEDStripDomain;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;


/**
 * ConfigurationLEDStripsController.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@RestController
@RequestMapping("/configuration/ledstrips")
public class ConfigurationLEDStripsController {

    private final LEDStripDTOService ledStripDTOService;

    @Autowired
    public ConfigurationLEDStripsController(LEDStripDTOService ledStripDTOService) {

        this.ledStripDTOService = ledStripDTOService;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = { "application/hal+json" })
    public Resources<LEDStripResource> getLEDStrips() {

        List<LEDStripDTO> ledStripDTOs;
        List<LEDStripResource> resources = new ArrayList<>();
        List<Link> links = new ArrayList<>();

        Link self = linkTo(ConfigurationLEDStripsController.class).withSelfRel();
        links.add(self);

        try {
            ledStripDTOs = ledStripDTOService.getAllDTOs();
        } catch (IOException exception) {
            throw new LoadFailedException("Error loading LED strips:", exception);
        }

        for (LEDStripDTO ledStripDTO : ledStripDTOs) {
            LEDStripDomain ledStripDomain = ledStripDTO.getDomain();

            self = linkTo(methodOn(ConfigurationLEDStripsController.class).getLEDStrip(ledStripDomain.getName()))
                .withSelfRel();

            LEDStripResource resource = new LEDStripResource(ledStripDomain, self);

            resources.add(resource);
        }

        return new Resources<>(resources, links);
    }


    @ResponseBody
    @RequestMapping(value = "/{name}", method = RequestMethod.GET, produces = { "application/hal+json" })
    public LEDStripResource getLEDStrip(@PathVariable String name) {

        LEDStripDomain ledStripDomain;

        try {
            ledStripDomain = ledStripDTOService.getDTO(name).getDomain();
        } catch (IOException | NullPointerException exception) {
            throw new LoadFailedException("Error loading LED strip:", exception);
        }

        Link self = linkTo(methodOn(ConfigurationLEDStripsController.class).getLEDStrip(ledStripDomain.getName()))
            .withSelfRel();

        return new LEDStripResource(ledStripDomain, self);
    }
}
