package org.synyx.sybil.bricklet.output.ledstrip.api;

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
import org.synyx.sybil.bricklet.output.ledstrip.Color;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStrip;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStripService;
import org.synyx.sybil.bricklet.output.ledstrip.database.LEDStripRepository;
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
 * ConfigurationLEDStripControllerTest.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { DevSpringConfig.class })
public class ConfigurationLEDStripControllerIntegTest {

    @Autowired
    StartupLoader startupLoader;

    @Autowired
    ConfigurationLEDStripController configurationLEDStripController;

    @Autowired
    LEDStripService outputLEDStripService;

    @Autowired
    LEDStripRepository outputLEDStripRepository;

    LEDStrip stubOne;
    LEDStrip stubTwo;
    LEDStrip stubThree;

    @Before
    public void setup() {

        startupLoader.init();

        stubOne = outputLEDStripService.getLEDStrip(outputLEDStripRepository.findByName("ledone"));
        stubTwo = outputLEDStripService.getLEDStrip(outputLEDStripRepository.findByName("ledtwo"));
        stubThree = outputLEDStripService.getLEDStrip(outputLEDStripRepository.findByName("ledthree"));

        stubOne.setFill(Color.BLACK);
        stubTwo.setFill(Color.BLACK);
        stubThree.setFill(Color.BLACK);

        stubOne.setBrightness(1.0);
        stubTwo.setBrightness(1.0);
        stubThree.setBrightness(1.0);
    }


    @Test
    public void testGetLEDStripConfiguration() throws Exception {

        MockMvc mockMvc = standaloneSetup(configurationLEDStripController).build();

        mockMvc.perform(get("/configuration/ledstrips/"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.links", hasSize(1)))
            .andExpect(jsonPath("$.links[0].rel", is("self")))
            .andExpect(jsonPath("$.links[0].href", endsWith("/configuration/ledstrips")))
            .andExpect(jsonPath("$.content", hasSize(3)))
            .andExpect(jsonPath("$.content[*].name", containsInAnyOrder("ledone", "ledtwo", "ledthree")))
            .andExpect(jsonPath("$.content[*].uid", containsInAnyOrder("def", "abc", "ghi")))
            .andExpect(jsonPath("$.content[*].length", containsInAnyOrder(5, 10, 20)))
            .andExpect(jsonPath("$.content[*].brick.name", containsInAnyOrder("stubone", "stubtwo", "stubthree")))
            .andExpect(jsonPath("$.content[*].brick.hostname", contains("localhost", "localhost", "localhost")))
            .andExpect(jsonPath("$.content[*].brick.port", containsInAnyOrder(14223, 14224, 14225)))
            .andExpect(jsonPath("$.content[0].links[0].href", containsString("/configuration/ledstrips/led")))
            .andExpect(jsonPath("$.content[1].links[0].href", containsString("/configuration/ledstrips/led")))
            .andExpect(jsonPath("$.content[2].links[0].href", containsString("/configuration/ledstrips/led")));

        mockMvc.perform(get("/configuration/ledstrips/ledone/"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.links", hasSize(2)))
            .andExpect(jsonPath("$.links[0].rel", is("self")))
            .andExpect(jsonPath("$.links[0].href", endsWith("/configuration/ledstrips/ledone")))
            .andExpect(jsonPath("$.links[1].rel", is("display")))
            .andExpect(jsonPath("$.links[1].href", endsWith("/configuration/ledstrips/ledone/display")))
            .andExpect(jsonPath("$.name", is("ledone")))
            .andExpect(jsonPath("$.uid", is("abc")))
            .andExpect(jsonPath("$.length", is(5)))
            .andExpect(jsonPath("$.brick.name", is("stubone")))
            .andExpect(jsonPath("$.brick.hostname", is("localhost")))
            .andExpect(jsonPath("$.brick.port", is(14223)));
    }


    @Test
    public void testGetDisplay() throws Exception {

        MockMvc mockMvc = standaloneSetup(configurationLEDStripController).build();

        mockMvc.perform(get("/configuration/ledstrips/ledone/display/"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.brightness", is(1.0)))
            .andExpect(jsonPath("$.pixels", hasSize(5)))
            .andExpect(jsonPath("$.pixels[0].red", is(0)))
            .andExpect(jsonPath("$.pixels[0].green", is(0)))
            .andExpect(jsonPath("$.pixels[0].blue", is(0)));

        stubOne.setFill(new Color(8, 64, 255));
        stubOne.setBrightness(.3);
        stubOne.updateDisplay();

        mockMvc.perform(get("/configuration/ledstrips/ledone/display/"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.brightness", is(0.3)))
            .andExpect(jsonPath("$.pixels", hasSize(5)))
            .andExpect(jsonPath("$.pixels[4].red", is(8)))
            .andExpect(jsonPath("$.pixels[4].green", is(64)))
            .andExpect(jsonPath("$.pixels[4].blue", is(255)));
    }


    @Test
    public void testPutDisplay() throws Exception {

        MockMvc mockMvc = standaloneSetup(configurationLEDStripController).build();

        stubOne.setPixel(0, new Color(255, 0, 0));
        stubOne.setPixel(1, new Color(0, 255, 0));
        stubOne.setPixel(2, new Color(0, 0, 255));
        stubOne.setPixel(3, new Color(255, 255, 255));
        stubOne.setPixel(4, new Color(0, 0, 0));

        stubOne.setBrightness(.2);

        stubOne.updateDisplay();

        mockMvc.perform(get("/configuration/ledstrips/ledone/display/"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.brightness", is(0.2)))
            .andExpect(jsonPath("$.pixels", hasSize(5)))
            .andExpect(jsonPath("$.pixels[*].red", contains(255, 0, 0, 255, 0)))
            .andExpect(jsonPath("$.pixels[*].green", contains(0, 255, 0, 255, 0)))
            .andExpect(jsonPath("$.pixels[*].blue", contains(0, 0, 255, 255, 0)));
    }


    @Test
    public void testPatchDisplay() throws Exception {

        MockMvc mockMvc = standaloneSetup(configurationLEDStripController).build();

        ObjectMapper mapper = new ObjectMapper();

        // create patch
        List<String> valuesOne = new ArrayList<>();
        valuesOne.add("0.4");

        SinglePatchResource patchOne = new SinglePatchResource();
        patchOne.setAction("set");
        patchOne.setTarget("brightness");
        patchOne.setValues(valuesOne);

        List<SinglePatchResource> patches = new ArrayList<>();
        patches.add(patchOne);

        // finalize patch
        List<String> emptyValues = new ArrayList<>();

        SinglePatchResource finalPatch = new SinglePatchResource();
        finalPatch.setAction("update");
        finalPatch.setTarget("display");
        finalPatch.setValues(emptyValues);

        patches.add(finalPatch);

        PatchResource patchResource = new PatchResource();
        patchResource.setPatches(patches);

        // perform patch
        mockMvc.perform(patch("/configuration/ledstrips/ledone/display/").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(patchResource)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.brightness", is(0.4)))
            .andExpect(jsonPath("$.pixels", hasSize(5)))
            .andExpect(jsonPath("$.pixels[*].red", contains(0, 0, 0, 0, 0)))
            .andExpect(jsonPath("$.pixels[*].green", contains(0, 0, 0, 0, 0)))
            .andExpect(jsonPath("$.pixels[*].blue", contains(0, 0, 0, 0, 0)));

        // create patch
        patches.clear();

        valuesOne.clear();
        valuesOne.add("255");
        valuesOne.add("128");
        valuesOne.add("64");

        patchOne.setAction("set");
        patchOne.setTarget("fill");
        patchOne.setValues(valuesOne);

        patches.add(patchOne);

        // finalize patch
        patches.add(finalPatch);

        // perform patch
        mockMvc.perform(patch("/configuration/ledstrips/ledone/display/").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(patchResource)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.pixels", hasSize(5)))
            .andExpect(jsonPath("$.pixels[*].red", contains(255, 255, 255, 255, 255)))
            .andExpect(jsonPath("$.pixels[*].green", contains(128, 128, 128, 128, 128)))
            .andExpect(jsonPath("$.pixels[*].blue", contains(64, 64, 64, 64, 64)));

        // create patch
        patches.clear();

        valuesOne.clear();
        valuesOne.add("0");
        valuesOne.add("255");
        valuesOne.add("255");
        valuesOne.add("255");

        patchOne.setAction("set");
        patchOne.setTarget("pixel");
        patchOne.setValues(valuesOne);

        patches.add(patchOne);

        // finalize patch
        patches.add(finalPatch);

        // perform patch
        mockMvc.perform(patch("/configuration/ledstrips/ledone/display/").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(patchResource)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.pixels", hasSize(5)))
            .andExpect(jsonPath("$.pixels[*].red", contains(255, 255, 255, 255, 255)))
            .andExpect(jsonPath("$.pixels[*].green", contains(255, 128, 128, 128, 128)))
            .andExpect(jsonPath("$.pixels[*].blue", contains(255, 64, 64, 64, 64)));

        // create patch
        patches.clear();

        valuesOne.clear();
        valuesOne.add("-1");

        patchOne.setAction("move");
        patchOne.setTarget("pixels");
        patchOne.setValues(valuesOne);

        patches.add(patchOne);

        // finalize patch
        patches.add(finalPatch);

        // perform patch
        mockMvc.perform(patch("/configuration/ledstrips/ledone/display/").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(patchResource)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.pixels", hasSize(5)))
            .andExpect(jsonPath("$.pixels[*].red", contains(255, 255, 255, 255, 255)))
            .andExpect(jsonPath("$.pixels[*].green", contains(128, 128, 128, 128, 255)))
            .andExpect(jsonPath("$.pixels[*].blue", contains(64, 64, 64, 64, 255)));

        // create patch
        patches.clear();

        valuesOne.clear();
        valuesOne.add("2");

        patchOne.setAction("move");
        patchOne.setTarget("pixels");
        patchOne.setValues(valuesOne);

        patches.add(patchOne);

        // finalize patch
        patches.add(finalPatch);

        // perform patch
        mockMvc.perform(patch("/configuration/ledstrips/ledone/display/").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(patchResource)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.pixels", hasSize(5)))
            .andExpect(jsonPath("$.pixels[*].red", contains(255, 255, 255, 255, 255)))
            .andExpect(jsonPath("$.pixels[*].green", contains(128, 255, 128, 128, 128)))
            .andExpect(jsonPath("$.pixels[*].blue", contains(64, 255, 64, 64, 64)));
    }
}
