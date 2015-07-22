package org.synyx.sybil.bricklet.input.illuminance.domain;

import org.junit.Test;

import org.synyx.sybil.AttributeEmptyException;


public class IlluminanceDTOUnitTest {

    @Test(expected = AttributeEmptyException.class)
    public void testGetDomainAndFail() throws Exception {

        IlluminanceDTO illuminanceDTO = new IlluminanceDTO();
        illuminanceDTO.getDomain();
    }
}
