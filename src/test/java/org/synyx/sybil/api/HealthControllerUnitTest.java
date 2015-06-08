package org.synyx.sybil.api;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.web.servlet.MockMvc;

import org.synyx.sybil.jenkins.domain.Status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


/**
 * HealthControllerTest.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

public class HealthControllerUnitTest {

    @Autowired
    HealthController healthController = new HealthController();

    @Test
    public void testHealthController() throws Exception {

        MockMvc mockMvc = standaloneSetup(healthController).build();

        mockMvc.perform(get("/health")).andExpect(status().isOk()).andExpect(content().string("\"OKAY\""));

        HealthController.setHealth(Status.WARNING, "testGetHealth");

        mockMvc.perform(get("/health")).andExpect(status().isOk()).andExpect(content().string("\"WARNING\""));

        HealthController.setHealth(Status.OKAY, "testGetHealth");

        mockMvc.perform(get("/health")).andExpect(status().isOk()).andExpect(content().string("\"OKAY\""));

        HealthController.setHealth(Status.CRITICAL, "someOtherSource");

        mockMvc.perform(get("/health")).andExpect(status().isOk()).andExpect(content().string("\"CRITICAL\""));

        HealthController.setHealth(Status.OKAY, "testGetHealth");

        mockMvc.perform(get("/health")).andExpect(status().isOk()).andExpect(content().string("\"CRITICAL\""));

        HealthController.setHealth(Status.WARNING, "someOtherSource");

        mockMvc.perform(get("/health")).andExpect(status().isOk()).andExpect(content().string("\"WARNING\""));

        HealthController.setHealth(Status.OKAY, "someOtherSource");

        mockMvc.perform(get("/health")).andExpect(status().isOk()).andExpect(content().string("\"OKAY\""));
    }
}
