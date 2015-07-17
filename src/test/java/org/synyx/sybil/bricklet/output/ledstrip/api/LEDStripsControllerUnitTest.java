package org.synyx.sybil.bricklet.output.ledstrip.api;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.Mock;

import org.mockito.runners.MockitoJUnitRunner;

import org.springframework.test.web.servlet.MockMvc;

import org.synyx.sybil.bricklet.output.ledstrip.LEDStripDTOService;
import org.synyx.sybil.bricklet.output.ledstrip.domain.LEDStripDTO;
import org.synyx.sybil.bricklet.output.ledstrip.domain.LEDStripDomain;

import java.io.IOException;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


@RunWith(MockitoJUnitRunner.class)
public class LEDStripsControllerUnitTest {

    MockMvc mockMvc;
    LEDStripsController LEDStripsController;

    @Mock
    LEDStripDTOService ledStripDTOServiceMock;

    @Before
    public void setUp() throws Exception {

        LEDStripDomain ledStripDomainOne = new LEDStripDomain("ledone", "uid", 10, "brick");
        LEDStripDomain ledStripDomainTwo = new LEDStripDomain("ledtwo", "abc", 30, "anotherbrick");

        LEDStripDTO ledStripDTOOne = new LEDStripDTO();
        LEDStripDTO ledStripDTOTwo = new LEDStripDTO();

        ledStripDTOOne.setDomain(ledStripDomainOne);
        ledStripDTOTwo.setDomain(ledStripDomainTwo);

        List<LEDStripDTO> ledStripDTOs = Arrays.asList(ledStripDTOOne, ledStripDTOTwo);

        when(ledStripDTOServiceMock.getAllDTOs()).thenReturn(ledStripDTOs);
        when(ledStripDTOServiceMock.getDTO("ledone")).thenReturn(ledStripDTOOne);
        when(ledStripDTOServiceMock.getDTO("ledtwo")).thenReturn(ledStripDTOTwo);

        LEDStripsController = new LEDStripsController(ledStripDTOServiceMock);
        mockMvc = standaloneSetup(LEDStripsController).build();
    }


    @Test
    public void testGetLEDStrips() throws Exception {

        mockMvc.perform(get("/configuration/ledstrips/"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.links", hasSize(1)))
            .andExpect(jsonPath("$.links[0].rel", is("self")))
            .andExpect(jsonPath("$.links[0].href", is("http://localhost/configuration/ledstrips")))
            .andExpect(jsonPath("$.content", hasSize(2)))
            .andExpect(jsonPath("$.content[*].name", contains("ledone", "ledtwo")))
            .andExpect(jsonPath("$.content[*].uid", contains("uid", "abc")))
            .andExpect(jsonPath("$.content[*].length", contains(10, 30)))
            .andExpect(jsonPath("$.content[*].brick", contains("brick", "anotherbrick")))
            .andExpect(jsonPath("$.content[0].links[0].href", is("http://localhost/configuration/ledstrips/ledone")))
            .andExpect(jsonPath("$.content[1].links[0].href", is("http://localhost/configuration/ledstrips/ledtwo")));
    }


    @Test
    public void testGetLEDStripsAndFailLoading() throws Exception {

        when(ledStripDTOServiceMock.getAllDTOs()).thenThrow(new IOException("This is a test"));

        mockMvc.perform(get("/configuration/ledstrips/")).andExpect(status().is5xxServerError());
    }


    @Test
    public void testGetLEDStripsAndFailGetDomain() throws Exception {

        LEDStripDTO ledStripDTO = new LEDStripDTO();

        when(ledStripDTOServiceMock.getAllDTOs()).thenReturn(Arrays.asList(ledStripDTO));

        mockMvc.perform(get("/configuration/ledstrips/")).andExpect(status().is5xxServerError());
    }


    @Test
    public void testGetLEDStrip() throws Exception {

        mockMvc.perform(get("/configuration/ledstrips/ledone/"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.links", hasSize(1)))
            .andExpect(jsonPath("$.links[0].rel", is("self")))
            .andExpect(jsonPath("$.links[0].href", is("http://localhost/configuration/ledstrips/ledone")))
            .andExpect(jsonPath("$.name", is("ledone")))
            .andExpect(jsonPath("$.uid", is("uid")))
            .andExpect(jsonPath("$.brick", is("brick")))
            .andExpect(jsonPath("$.length", is(10)));
    }


    @Test
    public void testGetLEDStripAndFail() throws Exception {

        mockMvc.perform(get("/configuration/ledstrips/aledstripthatsnotthere/")).andExpect(status().is5xxServerError());
    }
}
