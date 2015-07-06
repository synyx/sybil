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
import org.synyx.sybil.bricklet.output.ledstrip.LEDStripService;
import org.synyx.sybil.bricklet.output.ledstrip.Sprite1D;
import org.synyx.sybil.bricklet.output.ledstrip.database.LEDStripDomain;

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

    private LEDStripService ledStripService;

    @Autowired
    public ConfigurationLEDStripController(LEDStripService ledStripService) {

        this.ledStripService = ledStripService;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = { "application/hal+json" })
    public Resources<LEDStripResource> ledstrips() {

        List<LEDStripDomain> ledStrips = ledStripService.getAllDomains();
        List<LEDStripResource> resources = new ArrayList<>();
        List<Link> links = new ArrayList<>();

        Link self = linkTo(ConfigurationLEDStripController.class).withSelfRel();
        links.add(self);

        for (LEDStripDomain ledStripDomain : ledStrips) {
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

        LEDStripDomain ledStripDomain = ledStripService.getDomain(name);

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

        LEDStripDomain ledStripDomain = ledStripService.getDomain(name);
        LEDStrip ledStrip = ledStripService.getLEDStrip(ledStripDomain);

        Link self = linkTo(methodOn(ConfigurationLEDStripController.class).getDisplay(ledStripDomain.getName()))
            .withSelfRel();

        DisplayResource display = new DisplayResource();

        display.add(self);
        display.setPixels(ledStrip.getPixelBuffer());
        display.setBrightness(ledStrip.getBrightness());

        return display;
    }


    @ResponseBody
    @RequestMapping(value = "/{name}/display", method = RequestMethod.PUT, produces = { "application/hal+json" })
    public DisplayResource setDisplay(@PathVariable String name, @RequestBody DisplayResource display) {

        LEDStripDomain ledStripDomain = ledStripService.getDomain(name);
        LEDStrip ledStrip = ledStripService.getLEDStrip(ledStripDomain);

        if (display.getPixels() != null && display.getPixels().size() > 0) {
            Sprite1D pixels = new Sprite1D(display.getPixels().size(), "setDisplay", display.getPixels());
            ledStrip.drawSprite(pixels, 0);
        }

        display.setPixels(ledStrip.getPixelBuffer());

        if (display.getBrightness() != null) {
            ledStrip.setBrightness(display.getBrightness());
        } else {
            display.setBrightness(1.0);
        }

        ledStrip.updateDisplay();

        Link self = linkTo(methodOn(ConfigurationLEDStripController.class).getDisplay(ledStripDomain.getName()))
            .withSelfRel();

        display.add(self);

        return display;
    }


    // TODO: Non-existing LED strips give a NullPointerException. Should be 404!
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> errorHandler(Exception e) {

        String error = "Error parsing input: " + e.toString();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
