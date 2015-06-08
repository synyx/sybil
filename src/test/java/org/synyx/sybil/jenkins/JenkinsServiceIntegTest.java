package org.synyx.sybil.jenkins;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.synyx.sybil.bricklet.output.ledstrip.Color;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStrip;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStripRegistry;
import org.synyx.sybil.bricklet.output.ledstrip.SingleStatusOnLEDStrip;
import org.synyx.sybil.bricklet.output.ledstrip.SingleStatusOnLEDStripRegistry;
import org.synyx.sybil.bricklet.output.ledstrip.database.LEDStripRepository;
import org.synyx.sybil.config.ConfigLoader;
import org.synyx.sybil.config.DevSpringConfig;
import org.synyx.sybil.jenkins.config.JenkinsConfig;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.core.Is.is;


/**
 * JenkinsServiceTest.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { DevSpringConfig.class })
public class JenkinsServiceIntegTest {

    @Autowired
    JenkinsService jenkinsService;

    @Autowired
    ConfigLoader configLoader;

    @Autowired
    JenkinsConfig jenkinsConfig;

    @Autowired
    LEDStripRegistry outputLEDStripRegistry;

    @Autowired
    LEDStripRepository outputLEDStripRepository;

    @Autowired
    SingleStatusOnLEDStripRegistry singleStatusOnLEDStripRegistry;

    LEDStrip stubOne;
    LEDStrip stubTwo;
    LEDStrip stubThree;

    SingleStatusOnLEDStrip stubOneStatus;
    SingleStatusOnLEDStrip stubTwoStatus;
    SingleStatusOnLEDStrip stubThreeStatus;

    @Before
    public void setup() {

        configLoader.loadConfig();

        stubOne = outputLEDStripRegistry.get(outputLEDStripRepository.findByName("ledone"));
        stubTwo = outputLEDStripRegistry.get(outputLEDStripRepository.findByName("ledtwo"));
        stubThree = outputLEDStripRegistry.get(outputLEDStripRepository.findByName("ledthree"));

        stubOne.setFill(Color.BLACK);
        stubTwo.setFill(Color.BLACK);
        stubThree.setFill(Color.BLACK);

        stubOneStatus = singleStatusOnLEDStripRegistry.get(stubOne);
        stubTwoStatus = singleStatusOnLEDStripRegistry.get(stubTwo);
        stubThreeStatus = singleStatusOnLEDStripRegistry.get(stubThree);
    }


    @Test
    public void testCustomColor() {

        // CRITICAL
        jenkinsConfig.reset();
        jenkinsConfig.put("http://localhost:8081", "job", stubOneStatus);

        jenkinsService.handleJobs();

        Color blue = new Color(0, 0, 127);

        assertThat(stubOne.getPixel(0), is(blue));

        // WARNING
        jenkinsConfig.reset();
        jenkinsConfig.put("http://localhost:8083", "job", stubOneStatus);

        jenkinsService.handleJobs();

        assertThat(stubOne.getPixel(0), is(Color.WARNING));

        // OKAY
        jenkinsConfig.reset();
        jenkinsConfig.put("http://localhost:8085", "job", stubOneStatus);

        jenkinsService.handleJobs();

        Color grey = new Color(16, 16, 16);

        assertThat(stubOne.getPixel(0), is(grey));
    }


    @Test
    public void testJenkinsService() {

        Color blue = new Color(0, 0, 127);
        Color grey = new Color(16, 16, 16);

        // As configured
        jenkinsService.handleJobs();

        assertThat(stubOne.getPixel(0), is(blue));
        assertThat(stubTwo.getPixel(0), is(Color.WARNING));
        assertThat(stubThree.getPixel(0), is(Color.OKAY));

        // *_anime statuses
        jenkinsConfig.reset();
        jenkinsConfig.put("http://localhost:8082", "job", stubOneStatus);
        jenkinsConfig.put("http://localhost:8084", "job", stubTwoStatus);
        jenkinsConfig.put("http://localhost:8086", "job", stubThreeStatus);

        jenkinsService.handleJobs();

        assertThat(stubOne.getPixel(0), is(blue));
        assertThat(stubTwo.getPixel(0), is(Color.WARNING));
        assertThat(stubThree.getPixel(0), is(Color.OKAY));

        // undefined statuses
        jenkinsConfig.reset();
        jenkinsConfig.put("http://localhost:8082", "nojob", stubOneStatus);
        jenkinsConfig.put("http://localhost:8084", "nojob", stubTwoStatus);
        jenkinsConfig.put("http://localhost:8086", "nojob", stubThreeStatus);

        jenkinsService.handleJobs();

        assertThat(stubOne.getPixel(0), is(grey));
        assertThat(stubTwo.getPixel(0), is(Color.OKAY));
        assertThat(stubThree.getPixel(0), is(Color.OKAY));
    }
}
