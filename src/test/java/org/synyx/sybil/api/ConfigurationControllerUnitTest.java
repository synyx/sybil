package org.synyx.sybil.api;

import org.junit.Test;

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

public class ConfigurationControllerUnitTest {

    @Test
    public void testGetConfiguration() throws Exception {

        MockMvc mockMvc = standaloneSetup(new ConfigurationController()).build();

        mockMvc.perform(get("/configuration/"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.links", hasSize(6)))
            .andExpect(jsonPath("$.links[0].rel", is("self")))
            .andExpect(jsonPath("$.links[0].href", endsWith("/configuration")))
            .andExpect(jsonPath("$.links[1].rel", is("bricks")))
            .andExpect(jsonPath("$.links[1].href", endsWith("/configuration/bricks")))
            .andExpect(jsonPath("$.links[2].rel", is("ledstrips")))
            .andExpect(jsonPath("$.links[2].href", endsWith("/configuration/ledstrips")))
            .andExpect(jsonPath("$.links[3].rel", is("relays")))
            .andExpect(jsonPath("$.links[3].href", endsWith("/configuration/relays")))
            .andExpect(jsonPath("$.links[4].rel", is("buttons")))
            .andExpect(jsonPath("$.links[4].href", endsWith("/configuration/buttons")))
            .andExpect(jsonPath("$.links[5].rel", is("illuminancesensors")))
            .andExpect(jsonPath("$.links[5].href", endsWith("/configuration/illuminancesensors")));
    }
}
