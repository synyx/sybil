package org.synyx.sybil.bricklet.output.ledstrip.api;

import org.springframework.hateoas.ResourceSupport;

import org.synyx.sybil.bricklet.output.ledstrip.Color;

import java.util.List;


/**
 * DisplayResource.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class DisplayResource extends ResourceSupport {

    private List<Color> pixels;

    public List<Color> getPixels() {

        return pixels;
    }


    public void setPixels(List<Color> pixels) {

        this.pixels = pixels;
    }
}
