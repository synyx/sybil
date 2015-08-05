package org.synyx.sybil.bricklet.output.ledstrip.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hamcrest.Matchers;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.Mockito;

import org.mockito.runners.MockitoJUnitRunner;

import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import org.synyx.sybil.LoadFailedException;
import org.synyx.sybil.bricklet.output.ledstrip.Color;
import org.synyx.sybil.bricklet.output.ledstrip.dto.LEDStripDTO;
import org.synyx.sybil.bricklet.output.ledstrip.dto.LEDStripDTOService;
import org.synyx.sybil.bricklet.output.ledstrip.service.LEDStripNotFoundException;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;

import static org.hamcrest.Matchers.hasSize;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


@RunWith(MockitoJUnitRunner.class)
public class DisplayControllerUnitTest {

    @Mock
    private LEDStripDTOService ledStripDTOServiceMock;

    private DisplayController sut;
    private MockMvc mockMvc;
    private List<Color> colors;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUp() throws Exception {

        colors = new ArrayList<>();

        colors.add(Color.BLACK);
        colors.add(Color.CRITICAL);
        colors.add(Color.WARNING);
        colors.add(Color.OKAY);
        colors.add(Color.WHITE);

        when(ledStripDTOServiceMock.get("ledone")).thenReturn(new LEDStripDTO(colors));

        sut = new DisplayController(ledStripDTOServiceMock);
        mockMvc = standaloneSetup(sut).build();
    }


    @Test
    public void testGetMissingDisplay() throws Exception {

        when(ledStripDTOServiceMock.get("doesntexist")).thenThrow(new LEDStripNotFoundException(
                "LED strip is not configured."));

        mockMvc.perform(get("/configuration/ledstrips/doesntexist/display")).andExpect(status().isNotFound());
    }


    @Test
    public void testGetFailingDisplay() throws Exception {

        // setup
        when(ledStripDTOServiceMock.get("ledone")).thenThrow(new LoadFailedException("test"));

        sut = new DisplayController(ledStripDTOServiceMock);
        mockMvc = standaloneSetup(sut).build();

        // execution & verification
        mockMvc.perform(get("/configuration/ledstrips/ledone/display")).andExpect(status().isInternalServerError());
    }


    @Test
    public void testGetDisplay() throws Exception {

        mockMvc.perform(get("/configuration/ledstrips/ledone/display"))
            .andExpect(status().isOk())
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


    @Test
    public void putFullDisplay() throws Exception {

        // setup
        LEDStripDTO ledStripDTO = new LEDStripDTO(colors);

        // execution
        mockMvc.perform(put("/configuration/ledstrips/ledone/display").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(ledStripDTO)))
            .andExpect(status().isOk());

        // verification
        verify(ledStripDTOServiceMock).setColorsOfLEDStrip(eq("ledone"),
            Mockito.argThat(Matchers.<LEDStripDTO>hasProperty("pixels", Matchers.is(colors))));
    }


    @Test
    public void putPartialDisplay() throws Exception {

        // setup
        colors = new ArrayList<>();

        colors.add(Color.CRITICAL);
        colors.add(Color.WARNING);
        colors.add(Color.OKAY);

        LEDStripDTO ledStripDTO = new LEDStripDTO(colors);

        // execution
        mockMvc.perform(put("/configuration/ledstrips/ledone/display").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(ledStripDTO)))
            .andExpect(status().isOk());

        // verification
        verify(ledStripDTOServiceMock).setColorsOfLEDStrip(eq("ledone"),
            Mockito.argThat(Matchers.<LEDStripDTO>hasProperty("pixels", Matchers.is(colors))));
    }


    @Test
    public void putNoPixelsDisplay() throws Exception {

        // execution
        mockMvc.perform(put("/configuration/ledstrips/ledone/display").contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());

        // verification
        verify(ledStripDTOServiceMock, never()).setColorsOfLEDStrip(any(String.class), any(LEDStripDTO.class));
    }
}
