package org.synyx.sybil.bricklet.input.api;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

import org.synyx.sybil.bricklet.input.SensorType;
import org.synyx.sybil.bricklet.input.database.InputSensorDomain;


/**
 * SensorResource.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class SensorResource extends Resource<InputSensorDomain> {

    private InputSensorDomain domain;

    public SensorResource(InputSensorDomain content, Link... links) {

        super(content, links);
        domain = content;
    }


    public SensorResource(InputSensorDomain content, Iterable<Link> links) {

        super(content, links);
        domain = content;
    }

    public String getPins() {

        if (domain.getType() == SensorType.BUTTON) {
            String pins = Integer.toBinaryString(domain.getPins());

            return "0000".substring((pins.length())) + pins;
        } else {
            return null;
        }
    }


    public Integer getThreshold() {

        if (domain.getType() == SensorType.LUMINANCE) {
            return domain.getThreshold();
        } else {
            return null;
        }
    }


    public Double getMultiplier() {

        if (domain.getType() == SensorType.LUMINANCE) {
            return domain.getMultiplier();
        } else {
            return null;
        }
    }


    public Integer getTimeout() {

        if (domain.getType() == SensorType.MOTION) {
            return domain.getTimeout();
        } else {
            return null;
        }
    }
}
