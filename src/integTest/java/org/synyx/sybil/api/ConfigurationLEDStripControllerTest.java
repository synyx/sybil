package org.synyx.sybil.api;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import org.synyx.sybil.config.ConfigLoader;
import org.synyx.sybil.config.DevSpringConfig;
import org.synyx.sybil.database.OutputLEDStripRepository;
import org.synyx.sybil.out.Color;
import org.synyx.sybil.out.OutputLEDStrip;
import org.synyx.sybil.out.OutputLEDStripRegistry;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.endsWith;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { DevSpringConfig.class })
public class ConfigurationLEDStripControllerTest {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationLEDStripControllerTest.class);

    @Autowired
    ConfigLoader configLoader;

    @Autowired
    ConfigurationLEDStripController configurationLEDStripController;

    @Autowired
    OutputLEDStripRegistry outputLEDStripRegistry;

    @Autowired
    OutputLEDStripRepository outputLEDStripRepository;

    OutputLEDStrip stubOne;
    OutputLEDStrip stubTwo;
    OutputLEDStrip stubThree;

    @Before
    public void setup() {

        configLoader.loadConfig();

        stubOne = outputLEDStripRegistry.get(outputLEDStripRepository.findByName("stubone"));
        stubTwo = outputLEDStripRegistry.get(outputLEDStripRepository.findByName("stubtwo"));
        stubThree = outputLEDStripRegistry.get(outputLEDStripRepository.findByName("stubthree"));

        stubOne.setFill(Color.BLACK);
        stubTwo.setFill(Color.BLACK);
        stubThree.setFill(Color.BLACK);
    }


    @Test
    public void testGetConfiguration() throws Exception {

        MockMvc mockMvc = standaloneSetup(configurationLEDStripController).build();

        mockMvc.perform(get("/configuration/ledstrips/")).andExpect(status().isOk()).andExpect(jsonPath("$.links",
                hasSize(1))).andExpect(jsonPath("$.links[0].rel", is("self"))).andExpect(jsonPath("$.links[0].href",
                endsWith("/configuration/ledstrips"))).andExpect(jsonPath("$.content", hasSize(3))).andExpect(jsonPath(
                "$.content[*].name", containsInAnyOrder("stubone", "stubtwo", "stubthree"))).andExpect(jsonPath(
                "$.content[*].uid", containsInAnyOrder("def", "abc", "ghi"))).andExpect(jsonPath("$.content[*].length",
                containsInAnyOrder(5, 10, 20))).andExpect(jsonPath("$.content[*].brick.name",
                containsInAnyOrder("stubone", "stubtwo", "stubthree"))).andExpect(jsonPath(
                "$.content[*].brick.hostname", contains("localhost", "localhost", "localhost"))).andExpect(jsonPath(
                "$.content[*].brick.port", containsInAnyOrder(14223, 14224, 14225))).andExpect(jsonPath(
                "$.content[0].links[0].href", containsString("/configuration/ledstrips/stub"))).andExpect(jsonPath(
                "$.content[1].links[0].href", containsString("/configuration/ledstrips/stub"))).andExpect(jsonPath(
                "$.content[2].links[0].href", containsString("/configuration/ledstrips/stub")));
    }
}
