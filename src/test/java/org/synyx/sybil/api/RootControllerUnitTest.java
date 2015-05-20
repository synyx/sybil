package org.synyx.sybil.api;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import org.synyx.sybil.config.DevSpringConfig;

import static org.hamcrest.CoreMatchers.endsWith;

import static org.hamcrest.Matchers.hasSize;

import static org.hamcrest.core.Is.is;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


/**
 * RootControllerTest.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { DevSpringConfig.class })
public class RootControllerUnitTest {

    @Test
    public void testGetRoot() throws Exception {

        MockMvc mockMvc = standaloneSetup(new RootController()).build();

        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.links", hasSize(2)))
            .andExpect(jsonPath("$.links[0].rel", is("self")))
            .andExpect(jsonPath("$.links[0].href", endsWith("localhost")))
            .andExpect(jsonPath("$.links[1].rel", is("configuration")))
            .andExpect(jsonPath("$.links[1].href", endsWith("/configuration")));
    }
}
