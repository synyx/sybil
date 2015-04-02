package org.synyx.sybil.config;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.synyx.sybil.common.jenkins.JenkinsConfig;


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

    @Test
    public void testLoadConfig() throws Exception {

        configLoader.loadConfig();
    }
}
