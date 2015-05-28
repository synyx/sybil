package org.synyx.sybil.bricklet.output.relay.api;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

import org.synyx.sybil.bricklet.output.relay.database.OutputRelayDomain;


/**
 * RelayResource.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class RelayResource extends Resource<OutputRelayDomain> {

    private boolean relay1;
    private boolean relay2;

    public RelayResource(OutputRelayDomain content, Link... links) {

        super(content, links);
    }


    public RelayResource(OutputRelayDomain content, Iterable<Link> links) {

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
