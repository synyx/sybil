package org.synyx.sybil.bricklet.output.relay.api;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

import org.synyx.sybil.bricklet.output.relay.database.RelayDomain;


/**
 * RelayResource.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class RelayResource extends Resource<RelayDomain> {

    private boolean relay1;
    private boolean relay2;

    public RelayResource(RelayDomain content, Link... links) {

        super(content, links);
    }


    public RelayResource(RelayDomain content, Iterable<Link> links) {

        super(content, links);
    }

    public boolean getRelay2() {

        return relay2;
    }


    public void setRelay2(boolean relay2) {

        this.relay2 = relay2;
    }


    public boolean getRelay1() {

        return relay1;
    }


    public void setRelay1(boolean relay1) {

        this.relay1 = relay1;
    }


    public void setRelays(boolean relay1, boolean relay2) {

        this.relay1 = relay1;
        this.relay2 = relay2;
    }
}
