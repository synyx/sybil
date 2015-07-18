package org.synyx.sybil.bricklet.output.ledstrip.domain;

import org.junit.Test;

import org.synyx.sybil.AttributeEmptyException;

import static org.hamcrest.Matchers.is;

import static org.junit.Assert.assertThat;


public class LEDStripDomainUnitTest {

    @Test(expected = AttributeEmptyException.class)
    public void getSensorWhenNoneWasDefined() {

        LEDStripDomain ledStripDomain = new LEDStripDomain();
        ledStripDomain.getSensor();
    }


    @Test
    public void getSensor() {

        LEDStripDomain ledStripDomain = new LEDStripDomain();
        ledStripDomain.setSensor("a sensor");
        assertThat(ledStripDomain.getSensor(), is("a sensor"));
    }
}
