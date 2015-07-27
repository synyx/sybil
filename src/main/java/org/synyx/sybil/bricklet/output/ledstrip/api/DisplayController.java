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
import org.synyx.sybil.bricklet.output.ledstrip.Sprite1D;

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

    @Autowired
    public DisplayController(LEDStripDTOService ledStripDTOService) {

        this.ledStripDTOService = ledStripDTOService;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = { "application/json" })
    public List<Color> getDisplay(@PathVariable String name) {

        return ledStripDTOService.getPixels(name);
    }


    @ResponseBody
    @RequestMapping(method = RequestMethod.PUT, produces = "application/json")
    public List<Color> putDisplay(@PathVariable String name, @RequestBody List<Color> pixels) {

        if (pixels != null && !pixels.isEmpty()) {
            ledStripDTOService.handleSprite(name, new Sprite1D(pixels.size(), pixels));
        }

        return getDisplay(name);
    }


    @ExceptionHandler({ LEDStripNotFoundException.class, LEDStripConnectionException.class, LoadFailedException.class })
    public ResponseEntity<APIError> handleError(Exception exception) {

        return new ResponseEntity<>(new APIError(exception.getMessage()), NOT_FOUND);
    }
}
