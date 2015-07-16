package org.synyx.sybil.brick.api;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

import org.synyx.sybil.brick.domain.BrickDomain;


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
