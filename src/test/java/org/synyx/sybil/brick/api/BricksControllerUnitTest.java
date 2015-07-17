package org.synyx.sybil.brick.api;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.Mock;

import org.mockito.runners.MockitoJUnitRunner;

import org.springframework.test.web.servlet.MockMvc;

import org.synyx.sybil.brick.BrickDTOService;
import org.synyx.sybil.brick.domain.BrickDTO;
import org.synyx.sybil.brick.domain.BrickDomain;

import java.io.IOException;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


@RunWith(MockitoJUnitRunner.class)
public class BricksControllerUnitTest {

    MockMvc mockMvc;
    BricksController bricksController;

    @Mock
    BrickDTOService brickDTOServiceMock;

    @Before
    public void setUp() throws Exception {

        BrickDomain brickDomainOne = new BrickDomain("host", "uid");
        BrickDomain brickDomainTwo = new BrickDomain("anotherhost", "abc", 1234, "brick");

        BrickDTO brickDTOOne = new BrickDTO();
        BrickDTO brickDTOTwo = new BrickDTO();

        brickDTOOne.setDomain(brickDomainOne);
        brickDTOTwo.setDomain(brickDomainTwo);

        List<BrickDTO> brickDTOs = Arrays.asList(brickDTOOne, brickDTOTwo);

        when(brickDTOServiceMock.getAllDTOs()).thenReturn(brickDTOs);
        when(brickDTOServiceMock.getDTO("host")).thenReturn(brickDTOOne);
        when(brickDTOServiceMock.getDTO("brick")).thenReturn(brickDTOTwo);

        bricksController = new BricksController(brickDTOServiceMock);
        mockMvc = standaloneSetup(bricksController).build();
    }


    @Test
    public void testGetBricks() throws Exception {

        mockMvc.perform(get("/configuration/bricks/"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.links", hasSize(1)))
            .andExpect(jsonPath("$.links[0].rel", is("self")))
            .andExpect(jsonPath("$.links[0].href", endsWith("/configuration/bricks")))
            .andExpect(jsonPath("$.content", hasSize(2)))
            .andExpect(jsonPath("$.content[*].name", contains("host", "brick")))
            .andExpect(jsonPath("$.content[*].hostname", contains("host", "anotherhost")))
            .andExpect(jsonPath("$.content[*].uid", contains("uid", "abc")))
            .andExpect(jsonPath("$.content[*].port", contains(4223, 1234)))
            .andExpect(jsonPath("$.content[0].links[0].href", is("http://localhost/configuration/bricks/host")))
            .andExpect(jsonPath("$.content[1].links[0].href", is("http://localhost/configuration/bricks/brick")));
    }


    @Test
    public void testGetBricksAndFailLoading() throws Exception {

        when(brickDTOServiceMock.getAllDTOs()).thenThrow(new IOException("This is a test"));

        mockMvc.perform(get("/configuration/bricks/")).andExpect(status().is5xxServerError());
    }


    @Test
    public void testGetBricksAndFailGetDomain() throws Exception {

        BrickDTO brickDTO = new BrickDTO();

        when(brickDTOServiceMock.getAllDTOs()).thenReturn(Arrays.asList(brickDTO));

        mockMvc.perform(get("/configuration/bricks/")).andExpect(status().is5xxServerError());
    }


    @Test
    public void testGetBrick() throws Exception {

        mockMvc.perform(get("/configuration/bricks/brick/"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.links", hasSize(1)))
            .andExpect(jsonPath("$.links[0].rel", is("self")))
            .andExpect(jsonPath("$.links[0].href", is("http://localhost/configuration/bricks/brick")))
            .andExpect(jsonPath("$.name", is("brick")))
            .andExpect(jsonPath("$.uid", is("abc")))
            .andExpect(jsonPath("$.hostname", is("anotherhost")))
            .andExpect(jsonPath("$.port", is(1234)));
    }


    @Test
    public void testGetBrickAndFail() throws Exception {

        mockMvc.perform(get("/configuration/bricks/abrickthatsnotthere/")).andExpect(status().is5xxServerError());
    }
}
