package org.synyx.sybil.api;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.runners.MockitoJUnitRunner;

import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.endsWith;

import static org.hamcrest.Matchers.hasSize;

import static org.hamcrest.core.Is.is;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Test
    public void getConfiguration() throws Exception {

        MockMvc mockMvc = standaloneSetup(new ConfigurationController()).build();

        mockMvc.perform(get("/configuration/"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.links", hasSize(1)))
            .andExpect(jsonPath("$.links[0].rel", is("self")))
            .andExpect(jsonPath("$.links[0].href", endsWith("/configuration")));
    }
}
