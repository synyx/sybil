package org.synyx.sybil.brick;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.tinkerforge.BrickMaster;
import com.tinkerforge.IPConnection;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.mockito.runners.MockitoJUnitRunner;

import org.springframework.core.env.Environment;

import org.synyx.sybil.brick.database.BrickDomain;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;


/**
 * BrickConfigLoaderUnitTest.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@RunWith(MockitoJUnitRunner.class)
public class BrickConfigLoaderUnitTest {

    private BrickConfigLoader sut;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Environment environment;

    @Mock
    private BrickService brickService;

    @Mock
    private BrickMaster brickMaster;

    @Mock
    private IPConnection ipConnection;

    @Mock
    BrickDomain brickDomain;

    @Test
    public void testLoadBricksConfig() throws Exception {

        List<BrickDomain> bricks = new ArrayList<>();

        bricks.add(new BrickDomain("hostone", "abc123"));
        bricks.add(new BrickDomain("hosttwo", "def456", 666));
        bricks.add(new BrickDomain("hostthree", "ghi789", 1234, "nr3"));

        Mockito.when(environment.getProperty("path.to.configfiles")).thenReturn("/some/path/");
        Mockito.when(environment.getProperty("brick.reset.timeout.seconds")).thenReturn("15");

        Mockito.when(objectMapper.readValue(eq(new File("/some/path/bricks.json")), any(TypeReference.class)))
            .thenReturn(bricks);

        sut = new BrickConfigLoader(brickService, objectMapper, environment);

        sut.loadBricksConfig();

        Mockito.verify(objectMapper).readValue(eq(new File("/some/path/bricks.json")), any(TypeReference.class));

        InOrder inOrder = Mockito.inOrder(brickService);

        inOrder.verify(brickService).deleteAllDomains();
        inOrder.verify(brickService).saveDomains(bricks);
    }


    @Test
    public void testResetBricks() throws Exception {

        List<BrickDomain> bricks = new ArrayList<>();

        bricks.add(brickDomain);

        Mockito.when(environment.getProperty("brick.reset.timeout.seconds")).thenReturn("15");

        Mockito.when(brickService.getAllDomains()).thenReturn(bricks);

        Mockito.when(brickService.getIPConnection(brickDomain)).thenReturn(ipConnection);

        Mockito.when(brickService.getBrickMaster(brickDomain.getUid(), ipConnection)).thenReturn(brickMaster);

        Mockito.when(brickMaster.getChipTemperature()).thenReturn((short) 30);

        sut = new BrickConfigLoader(brickService, objectMapper, environment);

        sut.resetAllBricks();

        Mockito.verify(brickService).getBrickMaster(brickDomain.getUid(), ipConnection);

        InOrder inOrder = Mockito.inOrder(brickMaster);

        inOrder.verify(brickMaster).reset();
        inOrder.verify(brickMaster).getChipTemperature();

        Mockito.verify(brickService).disconnectAll();
    }
}
