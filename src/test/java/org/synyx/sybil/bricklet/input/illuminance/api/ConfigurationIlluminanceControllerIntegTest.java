package org.synyx.sybil.bricklet.input.illuminance.api;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import org.synyx.sybil.config.DevSpringConfig;
import org.synyx.sybil.config.StartupLoader;

import static org.hamcrest.CoreMatchers.endsWith;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;

import static org.hamcrest.core.Is.is;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


/**
 * ConfigurationIlluminanceControllerIntegTest.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { DevSpringConfig.class })
public class ConfigurationIlluminanceControllerIntegTest {

    @Autowired
    private StartupLoader startupLoader;

    @Autowired
    private ConfigurationIlluminanceController configurationIlluminanceController;

    @Before
    public void setup() {

        startupLoader.init();
    }


    @Test
    public void testGetIlluminanceSensorConfiguration() throws Exception {

        MockMvc mockMvc = standaloneSetup(configurationIlluminanceController).build();

        mockMvc.perform(get("/configuration/illuminancesensors/"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.links", hasSize(1)))
            .andExpect(jsonPath("$.links[0].rel", is("self")))
            .andExpect(jsonPath("$.links[0].href", endsWith("/configuration/illuminancesensors")))
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.content[*].name", containsInAnyOrder("lsdevkit")))
            .andExpect(jsonPath("$.content[*].uid", containsInAnyOrder("m3b")))
            .andExpect(jsonPath("$.content[*].threshold", containsInAnyOrder(16)))
            .andExpect(jsonPath("$.content[*].multiplier", containsInAnyOrder(2.5)))
            .andExpect(jsonPath("$.content[*].outputs[0]", containsInAnyOrder("ledthree")))
            .andExpect(jsonPath("$.content[*].links[0].rel", containsInAnyOrder("self")))
            .andExpect(jsonPath("$.content[0].links[0].href", containsString("/configuration/illuminancesensors/")));

        mockMvc.perform(get("/configuration/illuminancesensors/lsdevkit/"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.links", hasSize(1)))
            .andExpect(jsonPath("$.links[0].rel", is("self")))
            .andExpect(jsonPath("$.links[0].href", endsWith("/configuration/illuminancesensors/lsdevkit")))
            .andExpect(jsonPath("$.name", is("lsdevkit")))
            .andExpect(jsonPath("$.uid", is("m3b")))
            .andExpect(jsonPath("$.threshold", is(16)))
            .andExpect(jsonPath("$.multiplier", is(2.5)));
    }
}
