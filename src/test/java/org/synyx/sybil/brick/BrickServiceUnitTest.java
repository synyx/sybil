package org.synyx.sybil.brick;

import com.tinkerforge.BrickMaster;
import com.tinkerforge.IPConnection;
import com.tinkerforge.TimeoutException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.InOrder;
import org.mockito.Mock;

import org.powermock.core.classloader.annotations.PrepareForTest;

import org.powermock.modules.junit4.PowerMockRunner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.synyx.sybil.brick.domain.BrickDTO;
import org.synyx.sybil.brick.domain.BrickDomain;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ BrickService.class, LoggerFactory.class })
public class BrickServiceUnitTest {

    static Logger loggerMock;

    @Mock
    BrickDTOService brickDTOServiceMock;

    @Mock
    BrickMaster brickMasterMock;

    @Mock
    IPConnection ipConnectionMock;

    BrickService sut;

    @BeforeClass
    public static void setup() {

        mockStatic(LoggerFactory.class);

        loggerMock = mock(Logger.class);

        when(LoggerFactory.getLogger(any(Class.class))).thenReturn(loggerMock);
    }


    @AfterClass
    public static void verifyStatic() {

        verify(loggerMock).error(eq("Failed to reset bricks:"), any(Throwable.class));
    }


    @Test
    public void resetAllBricks() throws Exception {

        // setup
        BrickDTO brickDTO = new BrickDTO(new BrickDomain("host", "uid"));

        List<BrickDTO> brickDTOs = Arrays.asList(brickDTO);

        when(brickDTOServiceMock.getAllDTOs()).thenReturn(brickDTOs);

        whenNew(BrickMaster.class).withAnyArguments().thenReturn(brickMasterMock);

        whenNew(IPConnection.class).withNoArguments().thenReturn(ipConnectionMock);

        sut = new BrickService(brickDTOServiceMock);

        // execution
        sut.resetAllBricks();

        // verification
        InOrder inOrder = inOrder(brickMasterMock, ipConnectionMock);

        inOrder.verify(ipConnectionMock).connect("host", 4223);
        inOrder.verify(brickMasterMock).reset();
        inOrder.verify(ipConnectionMock).disconnect();
    }


    @Test
    public void resetAllBricksAndFail() throws Exception {

        // setup
        BrickDTO brickDTO = new BrickDTO(new BrickDomain("host", "uid"));

        List<BrickDTO> brickDTOs = Arrays.asList(brickDTO);

        when(brickDTOServiceMock.getAllDTOs()).thenReturn(brickDTOs);

        whenNew(BrickMaster.class).withAnyArguments().thenReturn(brickMasterMock);

        whenNew(IPConnection.class).withNoArguments().thenReturn(ipConnectionMock);

        doThrow(TimeoutException.class).when(brickMasterMock).reset();

        sut = new BrickService(brickDTOServiceMock);

        // execution
        sut.resetAllBricks();
    }
}
