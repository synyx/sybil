package org.synyx.sybil.brick;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.Mock;

import org.mockito.runners.MockitoJUnitRunner;

import org.springframework.core.env.Environment;

import org.synyx.sybil.brick.domain.BrickDTO;
import org.synyx.sybil.brick.domain.BrickDomain;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.is;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

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

    private List<BrickDomain> brickDomains = new ArrayList<>();

    @Before
    public void setup() throws IOException {

        BrickDomain one = new BrickDomain("host", "abc");
        BrickDomain two = new BrickDomain("host", "123", 4224, "anotherbrick");

        brickDomains.add(one);
        brickDomains.add(two);

        when(environmentMock.getProperty("path.to.configfiles")).thenReturn("path/to/config/files/");
        when(objectMapperMock.readValue(eq(new File("path/to/config/files/bricks.json")), any(TypeReference.class)))
            .thenReturn(brickDomains);

        sut = new BrickDTOService(objectMapperMock, environmentMock);
    }


    @Test
    public void getConfiguredDTOForSmallConstructor() throws Exception {

        BrickDTO brickDTO = sut.getDTO("host");

        assertThat(brickDTO.getDomain(), is(brickDomains.get(0)));
    }


    @Test
    public void getConfiguredDTOForBigConstructor() throws Exception {

        BrickDTO brickDTO = sut.getDTO("anotherbrick");

        assertThat(brickDTO.getDomain(), is(brickDomains.get(1)));
    }


    @Test(expected = BrickNotFoundException.class)
    public void getNonexistentBrickDTO() throws Exception {

        sut.getDTO("does not exist");
    }


    @Test
    public void getAllDTOs() throws Exception {

        List<BrickDTO> brickDTOs = sut.getAllDTOs();
        assertThat(brickDTOs.get(0).getDomain(), is(brickDomains.get(0)));
        assertThat(brickDTOs.get(1).getDomain(), is(brickDomains.get(1)));
    }
}
