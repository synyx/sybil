package org.synyx.sybil.bricklet.output.ledstrip.domain;

import org.synyx.sybil.bricklet.output.ledstrip.Color;

import java.util.List;

import javax.validation.constraints.NotNull;


/**
 * LEDStripDTO.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class LEDStripDTO {

    @NotNull
    private final List<Color> pixels;

    public LEDStripDTO(List<Color> pixels) {

        this.pixels = pixels;
    }


    public LEDStripDTO() {

        // default constructor deliberately left (almost) empty
        this.pixels = null;
    }

    public List<Color> getPixels() {

        return pixels;
    }
}
