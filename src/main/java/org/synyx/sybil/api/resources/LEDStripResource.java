package org.synyx.sybil.api.resources;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

import org.synyx.sybil.domain.OutputLEDStripDomain;


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
