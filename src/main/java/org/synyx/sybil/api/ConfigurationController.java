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

import org.synyx.sybil.config.ConfigLoader;

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

    ConfigLoader configLoader;

    @Autowired
    public ConfigurationController(ConfigLoader configLoader) {

        this.configLoader = configLoader;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = { "application/hal+json" })
    public Resources<Object> configuration() {

        List<Link> links = new ArrayList<>();

        links.add(linkTo(ConfigurationController.class).withSelfRel());
        links.add(linkTo(ConfigurationBricksController.class).withRel("bricks"));
        links.add(linkTo(ConfigurationLEDStripController.class).withRel("ledstrips"));

        return new Resources<>(Collections.emptySet(), links);
    }


    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, produces = { "application/hal+json" })
    public void updateJenkinsConfig() throws IOException {

        configLoader.loadJenkinsConfig();
    }


    @ResponseBody
    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> LoadingConfigurationFailed(IOException e) {

        LOG.error("Error loading jenkins.json: {}", e.toString());

        String error = "Error loading jenkins.json: " + e.getMessage();

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }
}
