package org.synyx.sybil.bricklet.input.illuminance.api;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

import org.synyx.sybil.bricklet.input.illuminance.database.IlluminanceSensorDomain;


/**
 * SensorResource.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class IlluminanceResource extends Resource<IlluminanceSensorDomain> {

    public IlluminanceResource(IlluminanceSensorDomain content, Link... links) {

        super(content, links);
    }


    public IlluminanceResource(IlluminanceSensorDomain content, Iterable<Link> links) {

        super(content, links);
    }
}
