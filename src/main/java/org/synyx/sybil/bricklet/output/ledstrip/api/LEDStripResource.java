package org.synyx.sybil.bricklet.output.ledstrip.api;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

import org.synyx.sybil.bricklet.output.ledstrip.database.OLdLEDStripDomain;


/**
 * LEDStripResource.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class LEDStripResource extends Resource<OLdLEDStripDomain> {

    public LEDStripResource(OLdLEDStripDomain content, Link... links) {

        super(content, links);
    }


    public LEDStripResource(OLdLEDStripDomain content, Iterable<Link> links) {

        super(content, links);
    }
}
