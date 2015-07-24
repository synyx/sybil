package org.synyx.sybil.bricklet.output.ledstrip.api;

import org.springframework.beans.factory.annotation.Autowired;

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
    @RequestMapping(method = RequestMethod.GET, produces = { "application/json" })
    public List<Color> getDisplay(@PathVariable String name) {

        LEDStripDTO ledStripDTO = ledStripDTOService.getDTO(name);

        return ledStripService.getPixels(ledStripDTO);
    }


    @ResponseBody
    @RequestMapping(method = RequestMethod.PUT, produces = "application/json")
    public List<Color> putDisplay(@PathVariable String name, @RequestBody List<Color> pixels) {

        if (pixels != null && !pixels.isEmpty()) {
            LEDStripDTO ledStripDTO = ledStripDTOService.getDTO(name);

            ledStripDTO.setSprite(new Sprite1D(pixels.size(), pixels));

            ledStripService.handleSprite(ledStripDTO);
        }

        return getDisplay(name);
    }


    @ExceptionHandler({ LEDStripNotFoundException.class, LEDStripConnectionException.class, LoadFailedException.class })
    public ResponseEntity<APIError> handleError(Exception exception) {

        return new ResponseEntity<>(new APIError(exception.getMessage()), NOT_FOUND);
    }
}
