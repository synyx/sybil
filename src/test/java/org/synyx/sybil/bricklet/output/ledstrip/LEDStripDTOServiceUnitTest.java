package org.synyx.sybil.bricklet.output.ledstrip;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.Mock;

import org.mockito.runners.MockitoJUnitRunner;

import org.springframework.core.env.Environment;

import org.synyx.sybil.AttributeEmptyException;
import org.synyx.sybil.LoadFailedException;
import org.synyx.sybil.bricklet.output.ledstrip.domain.LEDStripDTO;
import org.synyx.sybil.bricklet.output.ledstrip.domain.LEDStripDomain;

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
 * LEDStripDTOServiceUnitTest.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@RunWith(MockitoJUnitRunner.class)
public class LEDStripDTOServiceUnitTest {

    private LEDStripDTOService sut;

    @Mock
    private ObjectMapper objectMapperMock;

    @Mock
    private Environment environmentMock;

    private List<LEDStripDomain> ledStripDomains = new ArrayList<>();

    @Before
    public void setup() throws IOException {

        LEDStripDomain one = new LEDStripDomain("one", "abc", 42, "abrick");
        LEDStripDomain two = new LEDStripDomain("two", "xyz", 23, "anotherbrick");

        ledStripDomains.add(one);
        ledStripDomains.add(two);

        when(environmentMock.getProperty("path.to.configfiles")).thenReturn("path/to/config/files/");
        when(objectMapperMock.readValue(eq(new File("path/to/config/files/ledstrips.json")), any(TypeReference.class)))
            .thenReturn(ledStripDomains);

        sut = new LEDStripDTOService(objectMapperMock, environmentMock);
    }


    @Test
    public void getConfiguredDTO() throws Exception {

        LEDStripDTO ledStripDTO = sut.getDTO("two");

        assertThat(ledStripDTO.getDomain(), is(ledStripDomains.get(1)));
    }


    @Test(expected = LoadFailedException.class)
    public void getNonexistentLEDStripDTO() throws Exception {

        sut.getDTO("does not exist");
    }


    @Test(expected = AttributeEmptyException.class)
    public void getCustomColorsWhereNoneWereDefined() throws Exception {

        LEDStripDTO ledStripDTO = sut.getDTO("one");
        LEDStripDomain ledStripDomain = ledStripDTO.getDomain();
        ledStripDomain.getCustomColors();
    }
}
