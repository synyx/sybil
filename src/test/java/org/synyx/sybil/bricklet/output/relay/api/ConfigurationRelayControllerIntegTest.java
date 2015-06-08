package org.synyx.sybil.bricklet.output.relay.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import org.synyx.sybil.api.PatchResource;
import org.synyx.sybil.api.SinglePatchResource;
import org.synyx.sybil.bricklet.output.relay.EnumRelay;
import org.synyx.sybil.bricklet.output.relay.Relay;
import org.synyx.sybil.bricklet.output.relay.RelayRegistry;
import org.synyx.sybil.bricklet.output.relay.database.RelayRepository;
import org.synyx.sybil.config.DevSpringConfig;
import org.synyx.sybil.config.StartupLoader;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.endsWith;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

import static org.hamcrest.core.Is.is;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


/**
 * ConfigurationRelayControllerTest.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { DevSpringConfig.class })
public class ConfigurationRelayControllerIntegTest {

    @Autowired
    StartupLoader startupLoader;

    @Autowired
    ConfigurationRelayController configurationRelayController;

    @Autowired
    RelayRegistry outputRelayRegistry;

    @Autowired
    RelayRepository outputRelayRepository;

    ObjectMapper mapper = new ObjectMapper();

    Relay stubOne;
    Relay stubTwo;
    Relay stubThree;

    @Before
    public void setup() {

        startupLoader.init();

        stubOne = outputRelayRegistry.get(outputRelayRepository.findByName("relayone"));
        stubTwo = outputRelayRegistry.get(outputRelayRepository.findByName("relaytwo"));
        stubThree = outputRelayRegistry.get(outputRelayRepository.findByName("relaythree"));

        stubOne.setStates(false, false);
        stubTwo.setStates(false, false);
        stubThree.setStates(false, false);
    }


    @Test
    public void testGetRelayConfiguration() throws Exception {

        MockMvc mockMvc = standaloneSetup(configurationRelayController).build();

        mockMvc.perform(get("/configuration/relays/"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.links", hasSize(1)))
            .andExpect(jsonPath("$.links[0].rel", is("self")))
            .andExpect(jsonPath("$.links[0].href", endsWith("/configuration/relays")))
            .andExpect(jsonPath("$.content", hasSize(3)))
            .andExpect(jsonPath("$.content[*].name", containsInAnyOrder("relayone", "relaytwo", "relaythree")))
            .andExpect(jsonPath("$.content[*].uid", containsInAnyOrder("xxx", "yyy", "zzz")))
            .andExpect(jsonPath("$.content[*].brick.name", containsInAnyOrder("stubone", "stubtwo", "stubthree")))
            .andExpect(jsonPath("$.content[*].brick.hostname", contains("localhost", "localhost", "localhost")))
            .andExpect(jsonPath("$.content[*].brick.port", containsInAnyOrder(14223, 14224, 14225)))
            .andExpect(jsonPath("$.content[0].links[0].href", containsString("/configuration/relays/relay")))
            .andExpect(jsonPath("$.content[1].links[0].href", containsString("/configuration/relays/relay")))
            .andExpect(jsonPath("$.content[2].links[0].href", containsString("/configuration/relays/relay")))
            .andExpect(jsonPath("$.content[*].relay1", containsInAnyOrder(false, false, false)))
            .andExpect(jsonPath("$.content[*].relay2", containsInAnyOrder(false, false, false)));

        mockMvc.perform(get("/configuration/relays/relayone/"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.links", hasSize(1)))
            .andExpect(jsonPath("$.links[0].rel", is("self")))
            .andExpect(jsonPath("$.links[0].href", endsWith("/configuration/relays/relayone")))
            .andExpect(jsonPath("$.name", is("relayone")))
            .andExpect(jsonPath("$.uid", is("zzz")))
            .andExpect(jsonPath("$.brick.name", is("stubone")))
            .andExpect(jsonPath("$.brick.hostname", is("localhost")))
            .andExpect(jsonPath("$.brick.port", is(14223)))
            .andExpect(jsonPath("$.relay1", is(false)))
            .andExpect(jsonPath("$.relay2", is(false)));

        stubOne.setState(EnumRelay.ONE, true);

        mockMvc.perform(get("/configuration/relays/relayone/"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.relay1", is(true)))
            .andExpect(jsonPath("$.relay2", is(false)));

        stubOne.setState(EnumRelay.TWO, true);

        mockMvc.perform(get("/configuration/relays/relayone/"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.relay1", is(true)))
            .andExpect(jsonPath("$.relay2", is(true)));

        stubOne.setStates(false, false);

        mockMvc.perform(get("/configuration/relays/relayone/"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.relay1", is(false)))
            .andExpect(jsonPath("$.relay2", is(false)));
    }


    @Test
    public void testPatchRelayConfiguration() throws Exception {

        MockMvc mockMvc = standaloneSetup(configurationRelayController).build();

        // create patch
        List<String> values = new ArrayList<>();
        values.add("true");

        SinglePatchResource patch = new SinglePatchResource();
        patch.setAction("set");
        patch.setTarget("relay1");
        patch.setValues(values);

        List<SinglePatchResource> patches = new ArrayList<>();
        patches.add(patch);

        PatchResource patchResource = new PatchResource();
        patchResource.setPatches(patches);

        mockMvc.perform(patch("/configuration/relays/relayone").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(patchResource)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.relay1", is(true)))
            .andExpect(jsonPath("$.relay2", is(false)));

        values.clear();
        values.add("true");

        patch.setAction("set");
        patch.setTarget("relay2");
        patch.setValues(values);

        patches.clear();
        patches.add(patch);

        patchResource.setPatches(patches);

        mockMvc.perform(patch("/configuration/relays/relayone").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(patchResource)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.relay1", is(true)))
            .andExpect(jsonPath("$.relay2", is(true)));

        values.clear();
        values.add("false");
        values.add("false");

        patch.setAction("set");
        patch.setTarget("relays");
        patch.setValues(values);

        patches.clear();
        patches.add(patch);

        patchResource.setPatches(patches);

        mockMvc.perform(patch("/configuration/relays/relayone").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(patchResource)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.relay1", is(false)))
            .andExpect(jsonPath("$.relay2", is(false)));

        patch.setAction("toggle");
        patch.setTarget("relay1");

        patches.clear();
        patches.add(patch);

        patchResource.setPatches(patches);

        mockMvc.perform(patch("/configuration/relays/relayone").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(patchResource)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.relay1", is(true)))
            .andExpect(jsonPath("$.relay2", is(false)));

        patch.setAction("toggle");
        patch.setTarget("relay2");

        patches.clear();
        patches.add(patch);

        patchResource.setPatches(patches);

        mockMvc.perform(patch("/configuration/relays/relayone").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(patchResource)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.relay1", is(true)))
            .andExpect(jsonPath("$.relay2", is(true)));

        patch.setAction("toggle");
        patch.setTarget("relays");

        patches.clear();
        patches.add(patch);

        patchResource.setPatches(patches);

        mockMvc.perform(patch("/configuration/relays/relayone").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(patchResource)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.relay1", is(false)))
            .andExpect(jsonPath("$.relay2", is(false)));

        // test setting ALL relays at once
        values.clear();
        values.add("true");

        patch.setAction("set");
        patch.setTarget("relays");
        patch.setValues(values);

        patches.clear();
        patches.add(patch);

        patchResource.setPatches(patches);

        mockMvc.perform(patch("/configuration/relays/").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(patchResource)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.links", hasSize(1)))
            .andExpect(jsonPath("$.links[0].rel", is("self")))
            .andExpect(jsonPath("$.links[0].href", endsWith("/configuration/relays")))
            .andExpect(jsonPath("$.content", hasSize(3)))
            .andExpect(jsonPath("$.content[*].name", containsInAnyOrder("relayone", "relaytwo", "relaythree")))
            .andExpect(jsonPath("$.content[*].uid", containsInAnyOrder("xxx", "yyy", "zzz")))
            .andExpect(jsonPath("$.content[*].brick.name", containsInAnyOrder("stubone", "stubtwo", "stubthree")))
            .andExpect(jsonPath("$.content[*].brick.hostname", contains("localhost", "localhost", "localhost")))
            .andExpect(jsonPath("$.content[*].brick.port", containsInAnyOrder(14223, 14224, 14225)))
            .andExpect(jsonPath("$.content[0].links[0].href", containsString("/configuration/relays/relay")))
            .andExpect(jsonPath("$.content[1].links[0].href", containsString("/configuration/relays/relay")))
            .andExpect(jsonPath("$.content[2].links[0].href", containsString("/configuration/relays/relay")))
            .andExpect(jsonPath("$.content[*].relay1", containsInAnyOrder(true, true, true)))
            .andExpect(jsonPath("$.content[*].relay2", containsInAnyOrder(true, true, true)));
    }
}
