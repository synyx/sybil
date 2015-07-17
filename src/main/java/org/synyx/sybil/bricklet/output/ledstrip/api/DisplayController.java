package org.synyx.sybil.bricklet.output.ledstrip.api;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.hateoas.Link;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import org.synyx.sybil.LoadFailedException;
import org.synyx.sybil.bricklet.output.ledstrip.Color;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStripDTOService;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStripService;
import org.synyx.sybil.bricklet.output.ledstrip.Sprite1D;
import org.synyx.sybil.bricklet.output.ledstrip.domain.LEDStripDTO;

import java.io.IOException;

import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;


/**
 * DisplayController.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@RestController
@RequestMapping("/configuration/ledstrips/{name}/display")
public class DisplayController {

    private LEDStripDTOService ledStripDTOService;
    private LEDStripService ledStripService;

    @Autowired
    public DisplayController(LEDStripDTOService ledStripDTOService, LEDStripService ledStripService) {

        this.ledStripDTOService = ledStripDTOService;
        this.ledStripService = ledStripService;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = { "application/hal+json" })
    public DisplayResource getDisplay(@PathVariable String name) {

        LEDStripDTO ledStripDTO;

        try {
            ledStripDTO = ledStripDTOService.getDTO(name);
        } catch (IOException | NullPointerException exception) {
            throw new LoadFailedException("Error loading LED strip:", exception);
        }

        Link self = linkTo(methodOn(DisplayController.class).getDisplay(name)).withSelfRel();

        DisplayResource displayResource = new DisplayResource();

        displayResource.add(self);

        try {
            displayResource.setPixels(ledStripService.getPixels(ledStripDTO));
        } catch (IOException | TimeoutException | AlreadyConnectedException | NotConnectedException exception) {
            throw new LoadFailedException("Error getting Information from LED strip:", exception);
        }

        return displayResource;
    }


    @ResponseBody
    @RequestMapping(method = RequestMethod.PUT, produces = "application/hal+json")
    public DisplayResource putDisplay(@PathVariable String name, @RequestBody DisplayResource displayResource) {

        List<Color> pixels = displayResource.getPixels();

        if (pixels != null && !pixels.isEmpty()) {
            Sprite1D sprite1D;
            LEDStripDTO ledStripDTO;

            try {
                ledStripDTO = ledStripDTOService.getDTO(name);
            } catch (IOException | NullPointerException exception) {
                throw new LoadFailedException("Error loading LED strip:", exception);
            }

            sprite1D = new Sprite1D(pixels.size(), pixels);
            ledStripDTO.setSprite(sprite1D);

            try {
                ledStripService.handleSprite(ledStripDTO);
            } catch (TimeoutException | AlreadyConnectedException | IOException | NotConnectedException
                    | NullPointerException exception) {
                throw new LoadFailedException("Error setting pixels on LED strip:", exception);
            }
        }

        return getDisplay(name);
    }
}
