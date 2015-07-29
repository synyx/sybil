package org.synyx.sybil.brick;

import com.tinkerforge.BrickMaster;
import com.tinkerforge.IPConnection;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.Mock;

import org.powermock.core.classloader.annotations.PrepareForTest;

import org.powermock.modules.junit4.PowerMockRunner;

import org.synyx.sybil.brick.domain.BrickDTO;
import org.synyx.sybil.brick.domain.BrickDomain;

import static org.hamcrest.CoreMatchers.is;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.mockito.Mockito.verify;

import static org.powermock.api.mockito.PowerMockito.whenNew;


@RunWith(PowerMockRunner.class)
@PrepareForTest(BrickService.class)
public class BrickServiceUnitTest {

    private BrickDTO brickDTO;

    BrickService sut;

    @Mock
    private IPConnection ipConnectionMock;

    @Mock
    private BrickMaster brickMasterMock;

    @Before
    public void setup() throws Exception {

        BrickDomain brickDomain = new BrickDomain("host", "abc");
        brickDTO = new BrickDTO(brickDomain);

        whenNew(IPConnection.class).withNoArguments().thenReturn(ipConnectionMock);
        whenNew(BrickMaster.class).withAnyArguments().thenReturn(brickMasterMock);

        sut = new BrickService();
    }


    @Test
    public void connect() throws Exception {

        // execution
        IPConnection ipConnection = sut.connect(brickDTO);

        // verification
        verify(ipConnectionMock).connect("host", 4223);
        assertThat(ipConnection, is(ipConnectionMock));
    }


    @Test
    public void reset() throws Exception {

        // execution
        sut.reset(brickDTO);

        // verification
        verify(brickMasterMock).reset();
        verify(ipConnectionMock).disconnect();
    }
}
