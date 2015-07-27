package org.synyx.sybil.bricklet.output.ledstrip;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hamcrest.Matchers;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.mockito.runners.MockitoJUnitRunner;

import org.springframework.core.env.Environment;

import org.synyx.sybil.bricklet.output.ledstrip.domain.LEDStripDTO;
import org.synyx.sybil.bricklet.output.ledstrip.domain.LEDStripDomain;
import org.synyx.sybil.jenkins.domain.Status;
import org.synyx.sybil.jenkins.domain.StatusInformation;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;

import static org.junit.Assert.assertThat;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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

    @Mock
    private LEDStripService ledStripService;

    private List<LEDStripDomain> ledStripDomains = new ArrayList<>();

    LEDStripDomain one;
    LEDStripDomain two;

    @Before
    public void setup() throws IOException {

        one = new LEDStripDomain("one", "abc", 42, "abrick");
        two = new LEDStripDomain("two", "xyz", 23, "anotherbrick");

        ledStripDomains.add(one);
        ledStripDomains.add(two);

        when(environmentMock.getProperty("path.to.configfiles")).thenReturn("path/to/config/files/");
        when(objectMapperMock.readValue(eq(new File("path/to/config/files/ledstrips.json")), any(TypeReference.class)))
            .thenReturn(ledStripDomains);

        sut = new LEDStripDTOService(objectMapperMock, environmentMock, ledStripService);
    }


    @Test
    public void handleStatus() {

        // setup
        StatusInformation statusInformation = new StatusInformation("test", Status.OKAY);

        // execution
        sut.handleStatus("two", statusInformation);

        // verification
        ArgumentCaptor<LEDStripDTO> argumentCaptor = ArgumentCaptor.forClass(LEDStripDTO.class);

        verify(ledStripService).handleStatus(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getStatus(), is(statusInformation));
        assertThat(argumentCaptor.getValue().getDomain(), is(two));
    }


    @Test
    public void handleSprite() {

        // setup
        Sprite1D sprite1D = new Sprite1D(33);

        // execution
        sut.handleSprite("one", sprite1D);

        // verification
        ArgumentCaptor<LEDStripDTO> argumentCaptor = ArgumentCaptor.forClass(LEDStripDTO.class);

        verify(ledStripService).handleSprite(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getSprite(), is(sprite1D));
        assertThat(argumentCaptor.getValue().getDomain(), is(one));
    }


    @Test
    public void getPixels() {

        // setup
        List<Color> colors = new ArrayList<>();

        colors.add(Color.CRITICAL);
        colors.add(Color.WARNING);
        colors.add(Color.OKAY);

        when(ledStripService.getPixels(any(LEDStripDTO.class))).thenReturn(colors);

        // execution
        List<Color> result = sut.getPixels("one");

        // verification
        assertThat(result, is(colors));
    }


    @Test
    public void turnOffAllLEDStrips() {

        // executuion
        sut.turnOffAllLEDStrips();

        // verification
        verify(ledStripService).turnOff(Mockito.argThat(Matchers.<LEDStripDTO>hasProperty("domain", is(one))));
        verify(ledStripService).turnOff(Mockito.argThat(Matchers.<LEDStripDTO>hasProperty("domain", is(two))));
        verifyNoMoreInteractions(ledStripService);
    }


    @Test(expected = LEDStripNotFoundException.class)
    public void getNonexistentLEDStripDTO() throws Exception {

        sut.getPixels("doesnotexist");
    }
}
