package org.synyx.sybil.bricklet.output.ledstrip.api;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;

import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import org.synyx.sybil.LoadFailedException;
import org.synyx.sybil.bricklet.output.ledstrip.dto.LEDStripDTO;
import org.synyx.sybil.bricklet.output.ledstrip.dto.LEDStripDTOService;
import org.synyx.sybil.bricklet.output.ledstrip.service.LEDStripConnectionException;
import org.synyx.sybil.bricklet.output.ledstrip.service.LEDStripNotFoundException;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
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
    public LEDStripDTO getDisplay(@PathVariable String name) {

        return ledStripDTOService.get(name);
    }


    @ResponseBody
    @RequestMapping(method = RequestMethod.PUT, produces = "application/json")
    public LEDStripDTO putDisplay(@PathVariable String name, @Valid @RequestBody LEDStripDTO ledStripDTO,
        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new BadRequestException(bindingResult.getAllErrors().get(0).getObjectName()
                + " " + bindingResult.getAllErrors().get(0).getDefaultMessage());
        }

        ledStripDTOService.setColorsOfLEDStrip(name, ledStripDTO);

        return getDisplay(name);
    }


    @ExceptionHandler({ BadRequestException.class })
    public ResponseEntity<APIError> badRequestError(Exception exception) {

        return new ResponseEntity<>(new APIError(exception.getMessage()), BAD_REQUEST);
    }


    @ExceptionHandler({ LEDStripNotFoundException.class })
    public ResponseEntity<APIError> notFoundError(Exception exception) {

        return new ResponseEntity<>(new APIError(exception.getMessage()), NOT_FOUND);
    }


    @ExceptionHandler({ LEDStripConnectionException.class, LoadFailedException.class })
    public ResponseEntity<APIError> serverError(Exception exception) {

        return new ResponseEntity<>(new APIError(exception.getMessage()), INTERNAL_SERVER_ERROR);
    }
}
