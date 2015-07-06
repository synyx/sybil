package org.synyx.sybil.bricklet.output.ledstrip.api;

import org.springframework.hateoas.ResourceSupport;

import org.synyx.sybil.bricklet.output.ledstrip.OldColor;

import java.util.List;


/**
 * DisplayResource.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class DisplayResource extends ResourceSupport {

    private List<OldColor> pixels;

    public List<OldColor> getPixels() {

        return pixels;
    }


    public void setPixels(List<OldColor> pixels) {

        this.pixels = pixels;
    }
}
