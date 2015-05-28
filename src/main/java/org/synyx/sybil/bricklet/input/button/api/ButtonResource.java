package org.synyx.sybil.bricklet.input.button.api;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

import org.synyx.sybil.bricklet.input.button.database.ButtonDomain;


/**
 * SensorResource.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class ButtonResource extends Resource<ButtonDomain> {

    private ButtonDomain domain;

    public ButtonResource(ButtonDomain content, Link... links) {

        super(content, links);
        domain = content;
    }


    public ButtonResource(ButtonDomain content, Iterable<Link> links) {

        super(content, links);
        domain = content;
    }

    public String getPins() {

        String pins = Integer.toBinaryString(domain.getPins());

        return "0000".substring((pins.length())) + pins;
    }
}
