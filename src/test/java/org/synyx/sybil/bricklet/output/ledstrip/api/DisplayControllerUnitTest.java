package org.synyx.sybil.bricklet.output.ledstrip.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.Spy;

import org.mockito.runners.MockitoJUnitRunner;

import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import org.synyx.sybil.LoadFailedException;
import org.synyx.sybil.bricklet.output.ledstrip.Color;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStripDTOService;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStripNotFoundException;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStripService;
import org.synyx.sybil.bricklet.output.ledstrip.Sprite1D;
import org.synyx.sybil.bricklet.output.ledstrip.domain.LEDStripDTO;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.hasSize;

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
    private LEDStripService ledStripServiceMock;

    @Mock
    private LEDStripDTOService ledStripDTOServiceMock;

    @Spy
    private LEDStripDTO ledStripDTOMock;

    private DisplayController sut;
    private MockMvc mockMvc;
    private List<Color> colors;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUp() throws Exception {

        when(ledStripDTOServiceMock.getDTO("ledone")).thenReturn(ledStripDTOMock);

        colors = new ArrayList<>();

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

        when(ledStripDTOServiceMock.getDTO("doesntexist")).thenThrow(new LEDStripNotFoundException(
                "LED strip is not configured."));

        mockMvc.perform(get("/configuration/ledstrips/doesntexist/display")).andExpect(status().isNotFound());
    }


    @Test
    public void testGetFailingDisplay() throws Exception {

        // setup
        when(ledStripServiceMock.getPixels(ledStripDTOMock)).thenThrow(new LoadFailedException("test"));

        sut = new DisplayController(ledStripDTOServiceMock, ledStripServiceMock);
        mockMvc = standaloneSetup(sut).build();

        // execution & verification
        mockMvc.perform(get("/configuration/ledstrips/ledone/display")).andExpect(status().isNotFound());
    }


    @Test
    public void testGetDisplay() throws Exception {

        mockMvc.perform(get("/configuration/ledstrips/ledone/display"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(5)))
            .andExpect(jsonPath("$[0].red", is(0)))
            .andExpect(jsonPath("$[0].green", is(0)))
            .andExpect(jsonPath("$[0].blue", is(0)))
            .andExpect(jsonPath("$[1].red", is(127)))
            .andExpect(jsonPath("$[1].green", is(0)))
            .andExpect(jsonPath("$[1].blue", is(0)))
            .andExpect(jsonPath("$[2].red", is(127)))
            .andExpect(jsonPath("$[2].green", is(127)))
            .andExpect(jsonPath("$[2].blue", is(0)))
            .andExpect(jsonPath("$[3].red", is(0)))
            .andExpect(jsonPath("$[3].green", is(16)))
            .andExpect(jsonPath("$[3].blue", is(0)))
            .andExpect(jsonPath("$[4].red", is(255)))
            .andExpect(jsonPath("$[4].green", is(255)))
            .andExpect(jsonPath("$[4].blue", is(255)));
    }


    @Test
    public void putFullDisplay() throws Exception {

        // setup
        Sprite1D sprite1D = new Sprite1D(5, colors);

        // execution
        mockMvc.perform(put("/configuration/ledstrips/ledone/display").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(colors)))
            .andExpect(status().isOk());

        // verification
        assertThat(ledStripDTOMock.getSprite(), is(sprite1D));
        verify(ledStripServiceMock).handleSprite(ledStripDTOMock);
    }


    @Test
    public void putPartialDisplay() throws Exception {

        // setup
        colors = new ArrayList<>();

        colors.add(Color.CRITICAL);
        colors.add(Color.WARNING);
        colors.add(Color.OKAY);

        Sprite1D sprite1D = new Sprite1D(3, colors);

        // execution
        mockMvc.perform(put("/configuration/ledstrips/ledone/display").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(colors)))
            .andExpect(status().isOk());

        // verification
        assertThat(ledStripDTOMock.getSprite(), is(sprite1D));
        verify(ledStripServiceMock).handleSprite(ledStripDTOMock);
    }


    @Test
    public void putNoPixelsDisplay() throws Exception {

        // setup
        colors = new ArrayList<>();

        // execution
        mockMvc.perform(put("/configuration/ledstrips/ledone/display").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(colors)))
            .andExpect(status().isOk());

        // verification
        verify(ledStripServiceMock, never()).handleSprite(ledStripDTOMock);
    }
}
