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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


/**
 * ConfigurationSensorControllerTest.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { DevSpringConfig.class })
public class ConfigurationSensorControllerTest {

    @Autowired
    ConfigLoader configLoader;

    @Autowired
    ConfigurationSensorController configurationSensorController;

    @Before
    public void setup() {

        configLoader.loadConfig();
    }


    @Test
    public void testGetSensorConfiguration() throws Exception {

        MockMvc mockMvc = standaloneSetup(configurationSensorController).build();

        mockMvc.perform(get("/configuration/sensors/"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.links", hasSize(1)))
            .andExpect(jsonPath("$.links[0].rel", is("self")))
            .andExpect(jsonPath("$.links[0].href", endsWith("/configuration/sensors")))
            .andExpect(jsonPath("$.content", hasSize(5)))
            .andExpect(jsonPath("$.content[*].name",
                        containsInAnyOrder("lsdevkit", "mddevkit", "button1a", "button1b", "button2")))
            .andExpect(jsonPath("$.content[*].uid", containsInAnyOrder("m3b", "cCm", "aaa", "aaa", "bbb")))
            .andExpect(jsonPath("$.content[*].type",
                        containsInAnyOrder("LUMINANCE", "MOTION", "BUTTON", "BUTTON", "BUTTON")))
            .andExpect(jsonPath("$.content[*].threshold", containsInAnyOrder(16, null, null, null, null)))
            .andExpect(jsonPath("$.content[*].multiplier", containsInAnyOrder(2.5, null, null, null, null)))
            .andExpect(jsonPath("$.content[*].timeout", containsInAnyOrder(null, 120, null, null, null)))
            .andExpect(jsonPath("$.content[*].pins", containsInAnyOrder(null, null, "0001", "1000", "1111")))
            .andExpect(jsonPath("$.content[*].outputs[0]",
                        containsInAnyOrder("ledthree", "relayone", "relayone", "relaytwo", "relaythree")))
            .andExpect(jsonPath("$.content[*].links[0].rel",
                        containsInAnyOrder("self", "self", "self", "self", "self")))
            .andExpect(jsonPath("$.content[0].links[0].href", containsString("/configuration/sensors/")))
            .andExpect(jsonPath("$.content[1].links[0].href", containsString("/configuration/sensors/")))
            .andExpect(jsonPath("$.content[2].links[0].href", containsString("/configuration/sensors/")))
            .andExpect(jsonPath("$.content[3].links[0].href", containsString("/configuration/sensors/")))
            .andExpect(jsonPath("$.content[4].links[0].href", containsString("/configuration/sensors/")));

        mockMvc.perform(get("/configuration/sensors/button1a/"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.links", hasSize(1)))
            .andExpect(jsonPath("$.links[0].rel", is("self")))
            .andExpect(jsonPath("$.links[0].href", endsWith("/configuration/sensors/button1a")))
            .andExpect(jsonPath("$.name", is("button1a")))
            .andExpect(jsonPath("$.uid", is("aaa")))
            .andExpect(jsonPath("$.type", is("BUTTON")))
            .andExpect(jsonPath("$.threshold", is(nullValue())))
            .andExpect(jsonPath("$.multiplier", is(nullValue())))
            .andExpect(jsonPath("$.timeout", is(nullValue())))
            .andExpect(jsonPath("$.pins", is("0001")));

        mockMvc.perform(get("/configuration/sensors/lsdevkit/"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.links", hasSize(1)))
            .andExpect(jsonPath("$.links[0].rel", is("self")))
            .andExpect(jsonPath("$.links[0].href", endsWith("/configuration/sensors/lsdevkit")))
            .andExpect(jsonPath("$.name", is("lsdevkit")))
            .andExpect(jsonPath("$.uid", is("m3b")))
            .andExpect(jsonPath("$.type", is("LUMINANCE")))
            .andExpect(jsonPath("$.threshold", is(16)))
            .andExpect(jsonPath("$.multiplier", is(2.5)))
            .andExpect(jsonPath("$.timeout", is(nullValue())))
            .andExpect(jsonPath("$.pins", is(nullValue())));

        mockMvc.perform(get("/configuration/sensors/mddevkit/"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.links", hasSize(1)))
            .andExpect(jsonPath("$.links[0].rel", is("self")))
            .andExpect(jsonPath("$.links[0].href", endsWith("/configuration/sensors/mddevkit")))
            .andExpect(jsonPath("$.name", is("mddevkit")))
            .andExpect(jsonPath("$.uid", is("cCm")))
            .andExpect(jsonPath("$.type", is("MOTION")))
            .andExpect(jsonPath("$.threshold", is(nullValue())))
            .andExpect(jsonPath("$.multiplier", is(nullValue())))
            .andExpect(jsonPath("$.timeout", is(120)))
            .andExpect(jsonPath("$.pins", is(nullValue())));
    }
}
