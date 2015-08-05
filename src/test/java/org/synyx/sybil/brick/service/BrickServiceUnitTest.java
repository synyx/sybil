package org.synyx.sybil.brick.service;

import com.tinkerforge.BrickMaster;
import com.tinkerforge.IPConnection;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.Mock;

import org.powermock.core.classloader.annotations.PrepareForTest;

import org.powermock.modules.junit4.PowerMockRunner;

import org.synyx.sybil.brick.persistence.Brick;
import org.synyx.sybil.brick.persistence.BrickRepository;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.powermock.api.mockito.PowerMockito.whenNew;


@RunWith(PowerMockRunner.class)
@PrepareForTest(BrickService.class)
public class BrickServiceUnitTest {

    private Brick brick;

    private BrickService sut;

    @Mock
    private IPConnection ipConnectionMock;

    @Mock
    private BrickMaster brickMasterMock;

    @Mock
    BrickRepository brickRepository;

    @Before
    public void setup() throws Exception {

        brick = new Brick("host", "abc");

        Brick brickTwo = new Brick("host", "cde", 4224, "anotherbrick");

        List<Brick> bricks = Arrays.asList(brickTwo, brick);

        when(brickRepository.get("host")).thenReturn(brick);
        when(brickRepository.get("anotherbrick")).thenReturn(brickTwo);
        when(brickRepository.getAll()).thenReturn(bricks);

        whenNew(IPConnection.class).withNoArguments().thenReturn(ipConnectionMock);
        whenNew(BrickMaster.class).withAnyArguments().thenReturn(brickMasterMock);

        sut = new BrickService(brickRepository);
    }


    @Test
    public void connect() throws Exception {

        // execution
        IPConnection ipConnection = sut.connect("host");

        // verification
        verify(ipConnectionMock).connect("host", 4223);
        assertThat(ipConnection, is(ipConnectionMock));
    }


    @Test
    public void reset() throws Exception {

        // execution
        sut.resetAllBricks();

        // verification
        verify(brickMasterMock, times(2)).reset();
        verify(ipConnectionMock, times(2)).disconnect();
    }
}
