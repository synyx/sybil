package org.synyx.sybil.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import org.synyx.sybil.brick.api.ConfigurationBricksController;
import org.synyx.sybil.bricklet.input.illuminance.api.ConfigurationIlluminanceController;
import org.synyx.sybil.bricklet.output.ledstrip.api.ConfigurationLEDStripController;
import org.synyx.sybil.jenkins.JenkinsConfigLoader;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;


/**
 * ConfigurationController.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@RestController
@RequestMapping("/configuration")
public class ConfigurationController {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationController.class);

    private JenkinsConfigLoader jenkinsConfigLoader;

    @Autowired
    public ConfigurationController(JenkinsConfigLoader jenkinsConfigLoader) {

        this.jenkinsConfigLoader = jenkinsConfigLoader;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = { "application/hal+json" })
    public Resources<Object> configuration() {

        List<Link> links = new ArrayList<>();

        links.add(linkTo(ConfigurationController.class).withSelfRel());
        links.add(linkTo(ConfigurationBricksController.class).withRel("bricks"));
        links.add(linkTo(ConfigurationLEDStripController.class).withRel("ledstrips"));
        links.add(linkTo(ConfigurationIlluminanceController.class).withRel("illuminancesensors"));

        return new Resources<>(Collections.emptySet(), links);
    }


    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, produces = { "application/hal+json" })
    public void updateJenkinsConfig() throws IOException {

        jenkinsConfigLoader.loadJenkinsConfig();
    }


    @ResponseBody
    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> loadingConfigurationFailed(IOException e) {

        LOG.error("Error loading jenkins.json: {}", e.toString());

        String error = "Error loading jenkins.json: " + e.getMessage();

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }
}
