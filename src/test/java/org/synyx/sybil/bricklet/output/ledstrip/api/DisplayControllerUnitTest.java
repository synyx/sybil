package org.synyx.sybil.bricklet.output.ledstrip.api;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.Mock;

import org.mockito.runners.MockitoJUnitRunner;

import org.springframework.test.web.servlet.MockMvc;

import org.synyx.sybil.bricklet.output.ledstrip.Color;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStripDTOService;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStripService;
import org.synyx.sybil.bricklet.output.ledstrip.domain.LEDStripDTO;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;

import static org.hamcrest.Matchers.hasSize;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


@RunWith(MockitoJUnitRunner.class)
public class DisplayControllerUnitTest {

    @Mock
    LEDStripService ledStripServiceMock;

    @Mock
    LEDStripDTOService ledStripDTOServiceMock;

    @Mock
    LEDStripDTO ledStripDTOMock;

    DisplayController sut;
    MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {

        when(ledStripDTOServiceMock.getDTO("doesntexist")).thenThrow(new NullPointerException());
        when(ledStripDTOServiceMock.getDTO("ledone")).thenReturn(ledStripDTOMock);

        List<Color> colors = new ArrayList<>();
        colors.add(Color.BLACK);
        colors.add(Color.CRITICAL);
        colors.add(Color.WARNING);
        colors.add(Color.OKAY);
        colors.add(Color.WHITE);

        when(ledStripServiceMock.getPixels(ledStripDTOMock)).thenReturn(colors);

        sut = new DisplayController(ledStripDTOServiceMock, ledStripServiceMock);
        mockMvc = standaloneSetup(sut).build();
    }


    @Test
    public void testGetMissingDisplay() throws Exception {

        mockMvc.perform(get("/configuration/ledstrips/doesntexist/display")).andExpect(status().is5xxServerError());
    }


    @Test
    public void testGetFailingDisplay() throws Exception {

        when(ledStripServiceMock.getPixels(ledStripDTOMock)).thenThrow(new IOException());

        sut = new DisplayController(ledStripDTOServiceMock, ledStripServiceMock);
        mockMvc = standaloneSetup(sut).build();

        mockMvc.perform(get("/configuration/ledstrips/ledone/display")).andExpect(status().is5xxServerError());
    }


    @Test
    public void testGetDisplay() throws Exception {

        mockMvc.perform(get("/configuration/ledstrips/ledone/display"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.links", hasSize(1)))
            .andExpect(jsonPath("$.links[0].rel", is("self")))
            .andExpect(jsonPath("$.links[0].href", is("http://localhost/configuration/ledstrips/ledone/display")))
            .andExpect(jsonPath("$.pixels", hasSize(5)))
            .andExpect(jsonPath("$.pixels[0].red", is(0)))
            .andExpect(jsonPath("$.pixels[0].green", is(0)))
            .andExpect(jsonPath("$.pixels[0].blue", is(0)))
            .andExpect(jsonPath("$.pixels[1].red", is(127)))
            .andExpect(jsonPath("$.pixels[1].green", is(0)))
            .andExpect(jsonPath("$.pixels[1].blue", is(0)))
            .andExpect(jsonPath("$.pixels[2].red", is(127)))
            .andExpect(jsonPath("$.pixels[2].green", is(127)))
            .andExpect(jsonPath("$.pixels[2].blue", is(0)))
            .andExpect(jsonPath("$.pixels[3].red", is(0)))
            .andExpect(jsonPath("$.pixels[3].green", is(16)))
            .andExpect(jsonPath("$.pixels[3].blue", is(0)))
            .andExpect(jsonPath("$.pixels[4].red", is(255)))
            .andExpect(jsonPath("$.pixels[4].green", is(255)))
            .andExpect(jsonPath("$.pixels[4].blue", is(255)));
    }
}
