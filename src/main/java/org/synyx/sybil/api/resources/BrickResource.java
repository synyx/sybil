package org.synyx.sybil.api.resources;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

import org.synyx.sybil.domain.BrickDomain;


/**
 * BrickResource.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class BrickResource extends Resource<BrickDomain> {

    public BrickResource(BrickDomain content, Link... links) {

        super(content, links);
    }


    public BrickResource(BrickDomain content, Iterable<Link> links) {

        super(content, links);
    }
}
