package org.synyx.sybil.bricklet.output.ledstrip.api;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

import org.synyx.sybil.bricklet.output.ledstrip.database.OutputLEDStripDomain;


/**
 * LEDStripResource.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class LEDStripResource extends Resource<OutputLEDStripDomain> {

    public LEDStripResource(OutputLEDStripDomain content, Link... links) {

        super(content, links);
    }


    public LEDStripResource(OutputLEDStripDomain content, Iterable<Link> links) {

        super(content, links);
    }
}
