package org.synyx.sybil.bricklet.input.button.api;

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
 * ConfigurationButtonControllerIntegTest.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { DevSpringConfig.class })
public class ConfigurationButtonControllerIntegTest {

    @Autowired
    private StartupLoader startupLoader;

    @Autowired
    private ConfigurationButtonController configurationButtonController;

    @Before
    public void setup() {

        startupLoader.init();
    }


    @Test
    public void testGetButtonConfiguration() throws Exception {

        MockMvc mockMvc = standaloneSetup(configurationButtonController).build();

        mockMvc.perform(get("/configuration/buttons/"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.links", hasSize(1)))
            .andExpect(jsonPath("$.links[0].rel", is("self")))
            .andExpect(jsonPath("$.links[0].href", endsWith("/configuration/buttons")))
            .andExpect(jsonPath("$.content", hasSize(3)))
            .andExpect(jsonPath("$.content[*].name", containsInAnyOrder("button1a", "button1b", "button2")))
            .andExpect(jsonPath("$.content[*].uid", containsInAnyOrder("aaa", "aaa", "bbb")))
            .andExpect(jsonPath("$.content[*].pins", containsInAnyOrder("0001", "1000", "1111")))
            .andExpect(jsonPath("$.content[*].outputs[0]", containsInAnyOrder("relayone", "relaytwo", "relaythree")))
            .andExpect(jsonPath("$.content[*].links[0].rel", containsInAnyOrder("self", "self", "self")))
            .andExpect(jsonPath("$.content[0].links[0].href", containsString("/configuration/buttons/")))
            .andExpect(jsonPath("$.content[1].links[0].href", containsString("/configuration/buttons/")))
            .andExpect(jsonPath("$.content[2].links[0].href", containsString("/configuration/buttons/")));

        mockMvc.perform(get("/configuration/buttons/button1a/"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.links", hasSize(1)))
            .andExpect(jsonPath("$.links[0].rel", is("self")))
            .andExpect(jsonPath("$.links[0].href", endsWith("/configuration/buttons/button1a")))
            .andExpect(jsonPath("$.name", is("button1a")))
            .andExpect(jsonPath("$.uid", is("aaa")))
            .andExpect(jsonPath("$.pins", is("0001")));
    }
}
