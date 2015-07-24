package org.synyx.sybil.bricklet.output.ledstrip.api;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.hateoas.Link;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import org.synyx.sybil.LoadFailedException;
import org.synyx.sybil.bricklet.output.ledstrip.Color;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStripConnectionException;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStripDTOService;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStripNotFoundException;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStripService;
import org.synyx.sybil.bricklet.output.ledstrip.Sprite1D;
import org.synyx.sybil.bricklet.output.ledstrip.domain.LEDStripDTO;

import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import static org.springframework.http.HttpStatus.NOT_FOUND;


/**
 * DisplayController.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@RestController
@RequestMapping("/configuration/ledstrips/{name}/display")
public class DisplayController {

    private final LEDStripDTOService ledStripDTOService;
    private final LEDStripService ledStripService;

    @Autowired
    public DisplayController(LEDStripDTOService ledStripDTOService, LEDStripService ledStripService) {

        this.ledStripDTOService = ledStripDTOService;
        this.ledStripService = ledStripService;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = { "application/hal+json" })
    public DisplayResource getDisplay(@PathVariable String name) {

        LEDStripDTO ledStripDTO;

        ledStripDTO = ledStripDTOService.getDTO(name);

        Link self = linkTo(methodOn(DisplayController.class).getDisplay(name)).withSelfRel();

        DisplayResource displayResource = new DisplayResource();

        displayResource.add(self);

        displayResource.setPixels(ledStripService.getPixels(ledStripDTO));

        return displayResource;
    }


    @ResponseBody
    @RequestMapping(method = RequestMethod.PUT, produces = "application/hal+json")
    public DisplayResource putDisplay(@PathVariable String name, @RequestBody DisplayResource displayResource) {

        List<Color> pixels = displayResource.getPixels();

        if (pixels != null && !pixels.isEmpty()) {
            Sprite1D sprite1D;
            LEDStripDTO ledStripDTO;

            ledStripDTO = ledStripDTOService.getDTO(name);
            sprite1D = new Sprite1D(pixels.size(), pixels);
            ledStripDTO.setSprite(sprite1D);

            ledStripService.handleSprite(ledStripDTO);
        }

        return getDisplay(name);
    }


    @ExceptionHandler({ LEDStripNotFoundException.class, LEDStripConnectionException.class, LoadFailedException.class })
    public ResponseEntity<APIError> handleError(Exception exception) {

        return new ResponseEntity<>(new APIError(exception.getMessage()), NOT_FOUND);
    }
}
