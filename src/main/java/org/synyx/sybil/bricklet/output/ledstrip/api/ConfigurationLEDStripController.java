package org.synyx.sybil.bricklet.output.ledstrip.api;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

import org.neo4j.helpers.collection.IteratorUtil;

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
import org.synyx.sybil.bricklet.output.ledstrip.Color;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStrip;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStripService;
import org.synyx.sybil.bricklet.output.ledstrip.Sprite1D;
import org.synyx.sybil.bricklet.output.ledstrip.database.LEDStripDomain;
import org.synyx.sybil.bricklet.output.ledstrip.database.LEDStripRepository;

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

    private LEDStripRepository LEDStripRepository;
    private GraphDatabaseService graphDatabaseService;
    private LEDStripService LEDStripService;

    @Autowired
    public ConfigurationLEDStripController(LEDStripRepository LEDStripRepository,
        GraphDatabaseService graphDatabaseService, LEDStripService LEDStripService) {

        this.LEDStripRepository = LEDStripRepository;
        this.graphDatabaseService = graphDatabaseService;
        this.LEDStripService = LEDStripService;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = { "application/hal+json" })
    public Resources<LEDStripResource> ledstrips() {

        List<LEDStripDomain> ledStrips;
        List<LEDStripResource> resources = new ArrayList<>();
        List<Link> links = new ArrayList<>();

        Link self = linkTo(ConfigurationLEDStripController.class).withSelfRel();
        links.add(self);

        try(Transaction tx = graphDatabaseService.beginTx()) { // begin transaction

            // get all Bricks from database and cast them into a list so that they're actually fetched
            ledStrips = new ArrayList<>(IteratorUtil.asCollection(LEDStripRepository.findAll()));

            // end transaction
            tx.success();
        }

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

        LEDStripDomain ledStripDomain = LEDStripRepository.findByName(name);

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

        LEDStripDomain ledStripDomain = LEDStripRepository.findByName(name);
        LEDStrip ledStrip = LEDStripService.getLEDStrip(ledStripDomain);

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

        LEDStripDomain ledStripDomain = LEDStripRepository.findByName(name);
        LEDStrip ledStrip = LEDStripService.getLEDStrip(ledStripDomain);

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


    @ResponseBody
    @RequestMapping(value = "/{name}/display", method = RequestMethod.PATCH, produces = { "application/hal+json" })
    public DisplayResource updateDisplay(@PathVariable String name, @RequestBody PatchResource input) throws Exception {

        LEDStripDomain ledStripDomain = LEDStripRepository.findByName(name);
        LEDStrip ledStrip = LEDStripService.getLEDStrip(ledStripDomain);

        for (SinglePatchResource patch : input.getPatches()) {
            switch (patch.getAction()) {
                case "set":
                    switch (patch.getTarget()) {
                        case "brightness":
                            ledStrip.setBrightness(Double.parseDouble(patch.getValues().get(0)));
                            break;

                        case "fill": {
                            List<String> values = patch.getValues();
                            int red = Integer.parseInt(values.get(0));
                            int green = Integer.parseInt(values.get(1));
                            int blue = Integer.parseInt(values.get(2));
                            Color color = new Color(red, green, blue);
                            ledStrip.setFill(color);
                            break;
                        }

                        case "pixel": {
                            int index = Integer.parseInt(patch.getValues().get(0));
                            int red = Integer.parseInt(patch.getValues().get(1));
                            int green = Integer.parseInt(patch.getValues().get(2));
                            int blue = Integer.parseInt(patch.getValues().get(3));
                            Color color = new Color(red, green, blue);

                            ledStrip.setPixel(index, color);
                            break;
                        }

                        default:
                            throw new Exception("Unknown target for action set");
                    }

                    break;

                case "update":
                    switch (patch.getTarget()) {
                        case "display":
                            ledStrip.updateDisplay();
                            break;

                        default:
                            throw new Exception("Unknown target for action update");
                    }

                    break;

                case "move":
                    switch (patch.getTarget()) {
                        case "pixels": {
                            Sprite1D pixelbuffer = new Sprite1D(ledStrip.getLength(), "pixelbuffer",
                                    ledStrip.getPixelBuffer());

                            int offset = Integer.parseInt(patch.getValues().get(0));

                            if (offset < 0) {
                                offset = ledStrip.getLength() + offset;
                            }

                            if (offset > ledStrip.getLength()) {
                                offset = offset - ledStrip.getLength();
                            }

                            ledStrip.drawSprite(pixelbuffer, offset, true);
                        }
                    }

                    break;

                default:
                    throw new Exception("Unknown action");
            }
        }

        Link self = linkTo(methodOn(ConfigurationLEDStripController.class).getDisplay(ledStripDomain.getName()))
            .withSelfRel();

        DisplayResource display = new DisplayResource();

        display.add(self);
        display.setPixels(ledStrip.getPixelBuffer());
        display.setBrightness(ledStrip.getBrightness());

        return display;
    }


    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> errorHandler(Exception e) {

        String error = "Error parsing input: " + e.toString();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
