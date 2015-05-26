package org.synyx.sybil.config;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

import org.neo4j.helpers.collection.IteratorUtil;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import org.synyx.sybil.api.ConfigurationController;
import org.synyx.sybil.api.HealthController;
import org.synyx.sybil.common.jenkins.JenkinsConfig;
import org.synyx.sybil.database.BrickRepository;
import org.synyx.sybil.database.InputSensorRepository;
import org.synyx.sybil.database.OutputLEDStripRepository;
import org.synyx.sybil.database.OutputRelayRepository;
import org.synyx.sybil.domain.BrickDomain;
import org.synyx.sybil.domain.InputSensorDomain;
import org.synyx.sybil.domain.OutputLEDStripDomain;
import org.synyx.sybil.domain.OutputRelayDomain;
import org.synyx.sybil.in.SensorType;
import org.synyx.sybil.out.OutputLEDStripRegistry;
import org.synyx.sybil.out.SingleStatusOnLEDStrip;
import org.synyx.sybil.out.SingleStatusOnLEDStripRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.containsInAnyOrder;

import static org.hamcrest.core.Is.is;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


/**
 * ConfigLoaderTest.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { DevSpringConfig.class })
public class ConfigLoaderTest {

    @Autowired
    ConfigLoader configLoader;

    @Autowired
    JenkinsConfig jenkinsConfig;

    @Autowired
    BrickRepository brickRepository;

    @Autowired
    OutputLEDStripRepository outputLEDStripRepository;

    @Autowired
    OutputLEDStripRegistry outputLEDStripRegistry;

    @Autowired
    OutputRelayRepository outputRelayRepository;

    @Autowired
    InputSensorRepository inputSensorRepository;

    @Autowired
    SingleStatusOnLEDStripRegistry singleStatusOnLEDStripRegistry;

    @Autowired
    GraphDatabaseService graphDatabaseService;

    @Autowired
    HealthController healthController;

    @Autowired
    ConfigurationController configurationController;

    @Before
    public void setup() {

        configLoader.loadConfig();
    }


    @Test
    public void testLoadConfig() throws Exception {

        /**
         * Test loadBricksConfig
         */
        List<BrickDomain> bricks;

        try(Transaction tx = graphDatabaseService.beginTx()) { // begin transaction

            // get all Bricks from database and cast them into a list so that they're actually fetched
            bricks = new ArrayList<>(IteratorUtil.asCollection(brickRepository.findAll()));

            // end transaction
            tx.success();
        }

        assertThat(bricks.size(), is(3));

        for (BrickDomain brick : bricks) {
            assertThat(brick.getHostname(), is("localhost"));

            switch (brick.getName()) {
                case "stubOne":
                    assertThat(brick.getPort(), is("14223"));
                    break;

                case "stubTwo":
                    assertThat(brick.getPort(), is("14224"));
                    break;

                case "stubThree":
                    assertThat(brick.getPort(), is("14225"));
                    break;
            }
        }

        /**
         * Test loadLEDStripConfig
         */
        List<OutputLEDStripDomain> ledstrips;

        try(Transaction tx = graphDatabaseService.beginTx()) { // begin transaction

            // get all Bricks from database and cast them into a list so that they're actually fetched
            ledstrips = new ArrayList<>(IteratorUtil.asCollection(outputLEDStripRepository.findAll()));

            // end transaction
            tx.success();
        }

        assertThat(ledstrips.size(), is(3));

        for (OutputLEDStripDomain ledstrip : ledstrips) {
            switch (ledstrip.getName()) {
                case "ledone":
                    assertThat(ledstrip.getUid(), is("abc"));
                    assertThat(ledstrip.getBrickDomain(), is(brickRepository.findByName("stubone")));
                    assertThat(ledstrip.getLength(), is(5));
                    break;

                case "ledtwo":
                    assertThat(ledstrip.getUid(), is("def"));
                    assertThat(ledstrip.getBrickDomain(), is(brickRepository.findByName("stubtwo")));
                    assertThat(ledstrip.getLength(), is(10));
                    break;

                case "ledthree":
                    assertThat(ledstrip.getUid(), is("ghi"));
                    assertThat(ledstrip.getBrickDomain(), is(brickRepository.findByName("stubthree")));
                    assertThat(ledstrip.getLength(), is(20));
                    break;
            }
        }

        /**
         * Test loadRelayConfig
         */
        List<OutputRelayDomain> relays;

        try(Transaction tx = graphDatabaseService.beginTx()) { // begin transaction

            // get all Bricks from database and cast them into a list so that they're actually fetched
            relays = new ArrayList<>(IteratorUtil.asCollection(outputRelayRepository.findAll()));

            // end transaction
            tx.success();
        }

        assertThat(relays.size(), is(3));

        for (OutputRelayDomain relay : relays) {
            switch (relay.getName()) {
                case "relayone":
                    assertThat(relay.getUid(), is("zzz"));
                    assertThat(relay.getBrickDomain(), is(brickRepository.findByName("stubone")));
                    break;

                case "relaytwo":
                    assertThat(relay.getUid(), is("yyy"));
                    assertThat(relay.getBrickDomain(), is(brickRepository.findByName("stubtwo")));
                    break;

                case "relaythree":
                    assertThat(relay.getUid(), is("xxx"));
                    assertThat(relay.getBrickDomain(), is(brickRepository.findByName("stubthree")));
                    break;
            }
        }

        /**
         * Test loadSensorConfig
         */
        List<InputSensorDomain> sensors;

        try(Transaction tx = graphDatabaseService.beginTx()) { // begin transaction

            // get all Bricks from database and cast them into a list so that they're actually fetched
            sensors = new ArrayList<>(IteratorUtil.asCollection(inputSensorRepository.findAll()));

            // end transaction
            tx.success();
        }

        assertThat(sensors.size(), is(5));

        for (InputSensorDomain sensor : sensors) {
            switch (sensor.getName()) {
                case "lsdevkit":
                    assertThat(sensor.getUid(), is("m3b"));
                    assertThat(sensor.getType(), is(SensorType.LUMINANCE));
                    assertThat(sensor.getThreshold(), is(16));
                    assertThat(sensor.getMultiplier(), is(2.5));
                    assertThat(sensor.getBrickDomain(), is(brickRepository.findByName("stubthree")));
                    assertThat(sensor.getOutputs(), containsInAnyOrder("ledthree"));
                    break;

                case "mddevkit":
                    assertThat(sensor.getUid(), is("cCm"));
                    assertThat(sensor.getType(), is(SensorType.MOTION));
                    assertThat(sensor.getTimeout(), is(120));
                    assertThat(sensor.getBrickDomain(), is(brickRepository.findByName("stubtwo")));
                    assertThat(sensor.getOutputs(), containsInAnyOrder("relayone"));
                    break;

                case "button1a":
                    assertThat(sensor.getUid(), is("aaa"));
                    assertThat(sensor.getType(), is(SensorType.BUTTON));
                    assertThat(sensor.getPins(), is((short) 0b0001));
                    assertThat(sensor.getBrickDomain(), is(brickRepository.findByName("stubtwo")));
                    assertThat(sensor.getOutputs(), containsInAnyOrder("relayone"));
                    break;

                case "button1b":
                    assertThat(sensor.getUid(), is("aaa"));
                    assertThat(sensor.getType(), is(SensorType.BUTTON));
                    assertThat(sensor.getPins(), is((short) 0b1000));
                    assertThat(sensor.getBrickDomain(), is(brickRepository.findByName("stubtwo")));
                    assertThat(sensor.getOutputs(), containsInAnyOrder("relaytwo"));
                    break;

                case "button2":
                    assertThat(sensor.getUid(), is("bbb"));
                    assertThat(sensor.getType(), is(SensorType.BUTTON));
                    assertThat(sensor.getPins(), is((short) 0b1111));
                    assertThat(sensor.getBrickDomain(), is(brickRepository.findByName("stubone")));
                    assertThat(sensor.getOutputs(), containsInAnyOrder("relaythree"));
                    break;
            }
        }

        /**
         * Test loadJenkinsServers
         */
        Set<String> servers = jenkinsConfig.getServers();

        assertThat(servers.size(), is(6));
        assertTrue(servers.contains("http://localhost:8081"));
        assertTrue(servers.contains("http://localhost:8082"));
        assertTrue(servers.contains("http://localhost:8083"));
        assertTrue(servers.contains("http://localhost:8084"));
        assertTrue(servers.contains("http://localhost:8085"));
        assertTrue(servers.contains("http://localhost:8086"));

        /**
         * Test loadJenkinsConfig
         */
        Map<String, List<SingleStatusOnLEDStrip>> jobs;

        for (String server : servers) {
            jobs = jenkinsConfig.get(server);
            assertThat(jobs.get("job").size(), is(1));

            switch (server) {
                case "http://localhost:8081":
                case "http://localhost:8082":
                    assertThat(jobs.get("job").get(0),
                        is(singleStatusOnLEDStripRegistry.get(
                                outputLEDStripRegistry.get(outputLEDStripRepository.findByName("ledone")))));
                    break;

                case "http://localhost:8083":
                case "http://localhost:8084":
                    assertThat(jobs.get("job").get(0),
                        is(singleStatusOnLEDStripRegistry.get(
                                outputLEDStripRegistry.get(outputLEDStripRepository.findByName("ledtwo")))));
                    break;

                case "http://localhost:8085":
                case "http://localhost:8086":
                    assertThat(jobs.get("job").get(0),
                        is(singleStatusOnLEDStripRegistry.get(
                                outputLEDStripRegistry.get(outputLEDStripRepository.findByName("ledthree")))));
                    break;
            }
        }

        /**
         * Test HealthController
         */
        MockMvc mockMvc = standaloneSetup(healthController).build();

        mockMvc.perform(get("/health/")).andExpect(status().isOk()).andExpect(content().string("\"OKAY\""));
    }


    @Test
    public void testReloadConfig() throws Exception {

        /**
         * Load Configuration that will result in a warning.
         */
        configLoader.loadJenkinsConfig("jenkins_warn.json");

        /**
         * Test loadJenkinsConfig
         */
        Set<String> servers = jenkinsConfig.getServers();

        Map<String, List<SingleStatusOnLEDStrip>> jobs;

        for (String server : servers) {
            jobs = jenkinsConfig.get(server);

            switch (server) {
                case "http://localhost:8081":
                    assertNull(jobs);
                    break;

                case "http://localhost:8082":
                    assertThat(jobs.get("job").get(0),
                        is(singleStatusOnLEDStripRegistry.get(
                                outputLEDStripRegistry.get(outputLEDStripRepository.findByName("ledone")))));
                    break;

                case "http://localhost:8083":
                case "http://localhost:8084":
                    assertThat(jobs.get("job").get(0),
                        is(singleStatusOnLEDStripRegistry.get(
                                outputLEDStripRegistry.get(outputLEDStripRepository.findByName("ledtwo")))));
                    break;

                case "http://localhost:8085":
                case "http://localhost:8086":
                    assertThat(jobs.get("job").get(0),
                        is(singleStatusOnLEDStripRegistry.get(
                                outputLEDStripRegistry.get(outputLEDStripRepository.findByName("ledthree")))));
                    break;
            }
        }

        /**
         * Test HealthController
         */
        MockMvc healthMockMvc = standaloneSetup(healthController).build();

        healthMockMvc.perform(get("/health")).andExpect(status().isOk()).andExpect(content().string("\"WARNING\""));

        /**
         * Test Web Reloading
         */
        MockMvc configMockMvc = standaloneSetup(configurationController).build();

        configMockMvc.perform(post("/configuration")).andExpect(status().isOk());

        /**
         * Test to see if it's OKAY again!
         */
        healthMockMvc.perform(get("/health")).andExpect(status().isOk()).andExpect(content().string("\"OKAY\""));

        // TODO: Test for new feature: Reload configuration via PATCH, allow different config files. Test for failing config!
    }
}
