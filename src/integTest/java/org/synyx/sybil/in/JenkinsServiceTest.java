package org.synyx.sybil.in;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.synyx.sybil.common.jenkins.JenkinsConfig;
import org.synyx.sybil.config.ConfigLoader;
import org.synyx.sybil.config.DevSpringConfig;


/**
 * JenkinsServiceTest.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { DevSpringConfig.class })
public class JenkinsServiceTest {

    @Autowired
    JenkinsService jenkinsService;

    @Autowired
    ConfigLoader configLoader;

    @Autowired
    JenkinsConfig jenkinsConfig;

    @Test
    public void testJenkinsService() {

        // configLoader.loadConfig();
    }
}
