package org.synyx.sybil.bricklet.input.button.api;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.Mockito;

import org.mockito.runners.MockitoJUnitRunner;

import org.springframework.test.web.servlet.MockMvc;

import org.synyx.sybil.brick.database.BrickDomain;
import org.synyx.sybil.bricklet.input.button.ButtonService;
import org.synyx.sybil.bricklet.input.button.database.ButtonDomain;

import java.util.ArrayList;
import java.util.List;

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

@RunWith(MockitoJUnitRunner.class)
public class ConfigurationButtonControllerUnitTest {

    @Mock
    private ButtonService buttonService;

    private ConfigurationButtonController configurationButtonController;

    @Before
    public void setup() {

        BrickDomain brickDomain = new BrickDomain("localhost", "egal");

        List<ButtonDomain> buttonDomains = new ArrayList<>();

        List<String> outputs1 = new ArrayList<>();
        outputs1.add("relayone");

        List<String> outputs2 = new ArrayList<>();
        outputs2.add("relaytwo");

        List<String> outputs3 = new ArrayList<>();
        outputs3.add("relaythree");

        ButtonDomain button1a = new ButtonDomain("button1a", "aaa", (short) 1, outputs1, brickDomain);

        buttonDomains.add(button1a);
        buttonDomains.add(new ButtonDomain("button1b", "aaa", (short) 8, outputs2, brickDomain));
        buttonDomains.add(new ButtonDomain("button2", "bbb", (short) 15, outputs3, brickDomain));

        Mockito.when(buttonService.getAllDomains()).thenReturn(buttonDomains);

        Mockito.when(buttonService.getDomain("button1a")).thenReturn(button1a);

        configurationButtonController = new ConfigurationButtonController(buttonService);
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
