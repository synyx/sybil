package org.synyx.sybil.brick;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.mockito.runners.MockitoJUnitRunner;

import org.springframework.core.env.Environment;

import org.synyx.sybil.brick.database.BrickDomain;
import org.synyx.sybil.brick.database.BrickRepository;

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

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Environment environment;

    @Mock
    private BrickRepository brickRepository;

    private BrickConfigLoader brickConfigLoader;

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

        // dependencies not needed for testing this method are null
        brickConfigLoader = new BrickConfigLoader(brickRepository, null, objectMapper, null, environment);

        brickConfigLoader.loadBricksConfig();

        Mockito.verify(objectMapper).readValue(eq(new File("/some/path/bricks.json")), any(TypeReference.class));

        InOrder inOrder = Mockito.inOrder(brickRepository);

        inOrder.verify(brickRepository).deleteAll();
        inOrder.verify(brickRepository).save(bricks);
    }
}
