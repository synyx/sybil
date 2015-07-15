package org.synyx.sybil.brick;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.Mock;

import org.mockito.runners.MockitoJUnitRunner;

import org.synyx.sybil.AttributeEmptyException;
import org.synyx.sybil.brick.domain.BrickDTO;


@RunWith(MockitoJUnitRunner.class)
public class BrickServiceUnitTest {

    @Mock
    BrickDTOService brickDTOService;

    BrickService sut = new BrickService(brickDTOService);

    @Test(expected = AttributeEmptyException.class)
    public void connectEmtpyDTO() throws Exception {

        BrickDTO brickDTO = new BrickDTO();
        sut.connect(brickDTO);
    }
}
