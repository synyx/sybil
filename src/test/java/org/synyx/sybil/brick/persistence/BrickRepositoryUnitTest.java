package org.synyx.sybil.brick.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.Mock;

import org.mockito.runners.MockitoJUnitRunner;

import org.springframework.core.env.Environment;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

import static org.mockito.Mockito.when;


/**
 * BrickDTOServiceUnitTest.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@RunWith(MockitoJUnitRunner.class)
public class BrickRepositoryUnitTest {

    private BrickRepository sut;

    @Mock
    private ObjectMapper objectMapperMock;

    @Mock
    private Environment environmentMock;

    private List<Brick> bricks = new ArrayList<>();
    private Brick one;
    private Brick two;

    @Before
    public void setup() throws IOException {

        one = new Brick("host", "abc");

        two = new Brick("host", "123", 4224, "anotherbrick");

        bricks.add(one);
        bricks.add(two);

        when(environmentMock.getProperty("path.to.configfiles")).thenReturn("path/to/config/files/");
        when(objectMapperMock.readValue(eq(new File("path/to/config/files/bricks.json")), any(TypeReference.class)))
            .thenReturn(bricks);

        sut = new BrickRepository(objectMapperMock, environmentMock);
    }


    @Test
    public void getWithHostname() {

        Brick brick = sut.get("host");
        assertThat(brick, is(one));
    }


    @Test
    public void getWithName() {

        Brick brick = sut.get("anotherbrick");
        assertThat(brick, is(two));
    }


    @Test
    public void getNone() {

        Brick brick = sut.get("doesnotexist");
        assertThat(brick, is(nullValue()));
    }


    @Test
    public void getAll() {

        List<Brick> brickList = sut.getAll();

        assertThat(brickList, is(bricks));
    }
}
