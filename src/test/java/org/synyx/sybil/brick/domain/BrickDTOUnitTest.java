package org.synyx.sybil.brick.domain;

import org.junit.Test;

import org.synyx.sybil.AttributeEmptyException;


public class BrickDTOUnitTest {

    @Test(expected = AttributeEmptyException.class)
    public void getEmptyDomain() {

        BrickDTO brickDTO = new BrickDTO();
        brickDTO.getDomain();
    }
}
