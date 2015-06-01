package org.synyx.sybil.bricklet.output.ledstrip.api;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

import org.synyx.sybil.bricklet.output.ledstrip.database.LEDStripDomain;


/**
 * LEDStripResource.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class LEDStripResource extends Resource<LEDStripDomain> {

    public LEDStripResource(LEDStripDomain content, Link... links) {

        super(content, links);
    }


    public LEDStripResource(LEDStripDomain content, Iterable<Link> links) {

        super(content, links);
    }
}
