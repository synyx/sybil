package org.synyx.sybil.api;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import org.synyx.sybil.config.ConfigLoader;
import org.synyx.sybil.config.DevSpringConfig;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasSize;

import static org.hamcrest.core.Is.is;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


/**
 * ConfigurationBrickControllerTest.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { DevSpringConfig.class })
public class ConfigurationBrickControllerTest {

    @Autowired
    ConfigLoader configLoader;

    @Autowired
    ConfigurationBricksController configurationBricksController;

    @Before
    public void setup() {

        configLoader.loadConfig();
    }


    @Test
    public void testGetBricksConfiguration() throws Exception {

        MockMvc mockMvc = standaloneSetup(configurationBricksController).build();

        mockMvc.perform(get("/configuration/bricks/"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.links", hasSize(1)))
            .andExpect(jsonPath("$.links[0].rel", is("self")))
            .andExpect(jsonPath("$.links[0].href", endsWith("/configuration/bricks")))
            .andExpect(jsonPath("$.content", hasSize(3)))
            .andExpect(jsonPath("$.content[*].name", containsInAnyOrder("stubone", "stubtwo", "stubthree")))
            .andExpect(jsonPath("$.content[*].hostname", containsInAnyOrder("localhost", "localhost", "localhost")))
            .andExpect(jsonPath("$.content[*].port", containsInAnyOrder(14223, 14224, 14225)))
            .andExpect(jsonPath("$.content[0].links[0].href", containsString("/configuration/bricks/stub")))
            .andExpect(jsonPath("$.content[1].links[0].href", containsString("/configuration/bricks/stub")))
            .andExpect(jsonPath("$.content[2].links[0].href", containsString("/configuration/bricks/stub")));

        mockMvc.perform(get("/configuration/bricks/stubone/"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.links", hasSize(1)))
            .andExpect(jsonPath("$.links[0].rel", is("self")))
            .andExpect(jsonPath("$.links[0].href", endsWith("/configuration/bricks/stubone")))
            .andExpect(jsonPath("$.name", is("stubone")))
            .andExpect(jsonPath("$.hostname", is("localhost")))
            .andExpect(jsonPath("$.port", is(14223)));
    }
}
