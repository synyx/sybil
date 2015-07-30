package org.synyx.sybil.brick;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hamcrest.Matchers;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.Mockito;

import org.mockito.runners.MockitoJUnitRunner;

import org.springframework.core.env.Environment;

import org.synyx.sybil.brick.domain.BrickConfig;
import org.synyx.sybil.brick.domain.BrickDTO;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * BrickDTOServiceUnitTest.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@RunWith(MockitoJUnitRunner.class)
public class BrickDTOServiceUnitTest {

    private BrickDTOService sut;

    @Mock
    private ObjectMapper objectMapperMock;

    @Mock
    private Environment environmentMock;

    @Mock
    private BrickService brickServiceMock;

    private List<BrickConfig> brickConfigs = new ArrayList<>();
    private BrickConfig one;
    private BrickConfig two;

    @Before
    public void setup() throws IOException {

        one = new BrickConfig("host", "abc");

        two = new BrickConfig("host", "123", 4224, "anotherbrick");

        brickConfigs.add(one);
        brickConfigs.add(two);

        when(environmentMock.getProperty("path.to.configfiles")).thenReturn("path/to/config/files/");
        when(objectMapperMock.readValue(eq(new File("path/to/config/files/bricks.json")), any(TypeReference.class)))
            .thenReturn(brickConfigs);

        sut = new BrickDTOService(objectMapperMock, environmentMock, brickServiceMock);
    }


    @Test
    public void resetAllBricks() throws Exception {

        // execution
        sut.resetAllBricks();

        // verification
        verify(brickServiceMock).reset(Mockito.argThat(Matchers.<BrickDTO>hasProperty("config", is(one))));
        verify(brickServiceMock).reset(Mockito.argThat(Matchers.<BrickDTO>hasProperty("config", is(two))));
    }


    @Test
    public void connectWithHostname() throws Exception {

        // execution
        sut.connect("host");

        // verification
        verify(brickServiceMock).connect(Mockito.argThat(Matchers.<BrickDTO>hasProperty("config", is(one))));
    }


    @Test
    public void connectWithName() throws Exception {

        // execution
        sut.connect("anotherbrick");

        // verification
        verify(brickServiceMock).connect(Mockito.argThat(Matchers.<BrickDTO>hasProperty("config", is(two))));
    }


    @Test(expected = BrickNotFoundException.class)
    public void connectNonexistingBrick() throws Exception {

        sut.connect("nobrick");
    }
}
