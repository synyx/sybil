package org.synyx.sybil.bricklet.output.ledstrip.dto;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.Mock;

import org.mockito.runners.MockitoJUnitRunner;

import org.synyx.sybil.bricklet.output.ledstrip.Color;
import org.synyx.sybil.bricklet.output.ledstrip.persistence.LEDStripRepository;
import org.synyx.sybil.bricklet.output.ledstrip.service.LEDStripService;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;

import static org.junit.Assert.assertThat;

import static org.mockito.Matchers.eq;

import static org.mockito.Mockito.verify;
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
    private LEDStripRepository ledStripRepository;

    @Mock
    private LEDStripService ledStripService;

    List<Color> colors;

    @Before
    public void setup() throws IOException {

        colors = new ArrayList<>();

        colors.add(Color.CRITICAL);
        colors.add(Color.WARNING);
        colors.add(Color.OKAY);

        when(ledStripService.getPixels("one")).thenReturn(colors);

        sut = new LEDStripDTOService(ledStripService);
    }


    @Test
    public void get() {

        // execution
        LEDStripDTO result = sut.get("one");

        // verification
        assertThat(result.getPixels(), is(colors));
    }


    @Test
    public void setColorsOfLEDStrip() {

        // setup
        LEDStripDTO ledStripDTO = new LEDStripDTO(colors);

        // execution
        sut.setColorsOfLEDStrip("two", ledStripDTO);

        // verificatiom
        verify(ledStripService).setColors(eq("two"), eq(colors));
    }
}
