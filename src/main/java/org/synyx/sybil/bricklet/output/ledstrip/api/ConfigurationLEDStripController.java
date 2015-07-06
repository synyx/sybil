package org.synyx.sybil.bricklet.output.ledstrip.api;

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

import org.synyx.sybil.bricklet.output.ledstrip.LEDStrip;
import org.synyx.sybil.bricklet.output.ledstrip.OldColor;
import org.synyx.sybil.bricklet.output.ledstrip.OldLEDStripService;
import org.synyx.sybil.bricklet.output.ledstrip.OldSprite1D;
import org.synyx.sybil.bricklet.output.ledstrip.database.OLdLEDStripDomain;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

//TODO: Add 404 for non-existing LED Strips!


/**
 * ConfigurationLEDStripsController.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@RestController
@RequestMapping("/configuration/ledstrips")
public class ConfigurationLEDStripController {

    private OldLEDStripService ledStripService;

    @Autowired
    public ConfigurationLEDStripController(OldLEDStripService ledStripService) {

        this.ledStripService = ledStripService;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = { "application/hal+json" })
    public Resources<LEDStripResource> ledstrips() {

        List<OLdLEDStripDomain> ledStrips = ledStripService.getAllDomains();
        List<LEDStripResource> resources = new ArrayList<>();
        List<Link> links = new ArrayList<>();

        Link self = linkTo(ConfigurationLEDStripController.class).withSelfRel();
        links.add(self);

        for (OLdLEDStripDomain ledStripDomain : ledStrips) {
            self = linkTo(methodOn(ConfigurationLEDStripController.class).ledStrip(ledStripDomain.getName()))
                .withSelfRel();

            LEDStripResource resource = new LEDStripResource(ledStripDomain, self);

            resources.add(resource);
        }

        return new Resources<>(resources, links);
    }


    @ResponseBody
    @RequestMapping(value = "/{name}", method = RequestMethod.GET, produces = { "application/hal+json" })
    public LEDStripResource ledStrip(@PathVariable String name) {

        OLdLEDStripDomain ledStripDomain = ledStripService.getDomain(name);

        List<Link> links = new ArrayList<>();

        links.add(linkTo(methodOn(ConfigurationLEDStripController.class).ledStrip(ledStripDomain.getName()))
            .withSelfRel());
        links.add(linkTo(methodOn(ConfigurationLEDStripController.class).getDisplay(ledStripDomain.getName())).withRel(
                "display"));

        return new LEDStripResource(ledStripDomain, links);
    }


    @ResponseBody
    @RequestMapping(value = "/{name}/display", method = RequestMethod.GET, produces = { "application/hal+json" })
    public DisplayResource getDisplay(@PathVariable String name) {

        OLdLEDStripDomain ledStripDomain = ledStripService.getDomain(name);
        LEDStrip ledStrip = ledStripService.getLEDStrip(ledStripDomain);

        Link self = linkTo(methodOn(ConfigurationLEDStripController.class).getDisplay(ledStripDomain.getName()))
            .withSelfRel();

        DisplayResource display = new DisplayResource();

        display.add(self);
        display.setPixels(ledStrip.getPixelBuffer());

        return display;
    }


    @ResponseBody
    @RequestMapping(value = "/{name}/display", method = RequestMethod.PUT, produces = { "application/hal+json" })
    public DisplayResource setDisplay(@PathVariable String name, @RequestBody DisplayResource display) {

        OLdLEDStripDomain ledStripDomain = ledStripService.getDomain(name);
        LEDStrip ledStrip = ledStripService.getLEDStrip(ledStripDomain);

        if (wasLoaded(display.getPixels())) {
            OldSprite1D pixels = new OldSprite1D(display.getPixels().size(), "setDisplay", display.getPixels());
            ledStrip.drawSprite(pixels, 0);
            ledStrip.updateDisplay();
        }

        display.setPixels(ledStrip.getPixelBuffer());

        Link self = linkTo(methodOn(ConfigurationLEDStripController.class).getDisplay(ledStripDomain.getName()))
            .withSelfRel();

        display.add(self);

        return display;
    }


    private boolean wasLoaded(List<OldColor> pixels) {

        return pixels != null && !pixels.isEmpty();
    }


    // TODO: Non-existing LED strips give a NullPointerException. Should be 404!
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> errorHandler(Exception e) {

        String error = "Error parsing input: " + e.toString();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
