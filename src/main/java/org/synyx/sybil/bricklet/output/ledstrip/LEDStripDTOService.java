package org.synyx.sybil.bricklet.output.ledstrip;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.synyx.sybil.bricklet.output.ledstrip.domain.LEDStripDTO;

import java.util.List;


/**
 * LEDStripDTOService.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Service
public class LEDStripDTOService {

    private final LEDStripService ledStripService;

    @Autowired
    public LEDStripDTOService(LEDStripService ledStripService) {

        this.ledStripService = ledStripService;
    }

    public void setColorsOfLEDStrip(String name, LEDStripDTO ledStripDTO) {

        ledStripService.handleSprite(name, new Sprite1D(ledStripDTO.getPixels()));
    }


    public LEDStripDTO get(String name) {

        List<Color> pixels = ledStripService.getPixels(name);

        return new LEDStripDTO(pixels);
    }
}
