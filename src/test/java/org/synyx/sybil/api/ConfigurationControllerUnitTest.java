package org.synyx.sybil.api;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.Mock;

import org.mockito.runners.MockitoJUnitRunner;

import org.springframework.test.web.servlet.MockMvc;

import org.synyx.sybil.jenkins.JenkinsConfigLoader;

import static org.hamcrest.CoreMatchers.endsWith;

import static org.hamcrest.Matchers.hasSize;

import static org.hamcrest.core.Is.is;

import static org.mockito.Mockito.verify;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


/**
 * ConfigurationLEDStripControllerTest.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@RunWith(MockitoJUnitRunner.class)
public class ConfigurationControllerUnitTest {

    @Mock
    JenkinsConfigLoader jenkinsConfigLoader;

    @Test
    public void getConfiguration() throws Exception {

        MockMvc mockMvc = standaloneSetup(new ConfigurationController(jenkinsConfigLoader)).build();

        mockMvc.perform(get("/configuration/"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.links", hasSize(4)))
            .andExpect(jsonPath("$.links[0].rel", is("self")))
            .andExpect(jsonPath("$.links[0].href", endsWith("/configuration")))
            .andExpect(jsonPath("$.links[1].rel", is("bricks")))
            .andExpect(jsonPath("$.links[1].href", endsWith("/configuration/bricks")))
            .andExpect(jsonPath("$.links[2].rel", is("ledstrips")))
            .andExpect(jsonPath("$.links[2].href", endsWith("/configuration/ledstrips")))
            .andExpect(jsonPath("$.links[3].rel", is("illuminancesensors")))
            .andExpect(jsonPath("$.links[3].href", endsWith("/configuration/illuminancesensors")));
    }


    @Test
    public void loadNewConfig() throws Exception {

        MockMvc mockMvc = standaloneSetup(new ConfigurationController(jenkinsConfigLoader)).build();

        mockMvc.perform(post("/configuration/")).andExpect(status().isOk());

        verify(jenkinsConfigLoader).loadJenkinsConfig();
    }
}
