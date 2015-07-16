package org.synyx.sybil.api;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import org.synyx.sybil.brick.api.ConfigurationBricksController;

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

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = { "application/hal+json" })
    public Resources<Object> configuration() {

        List<Link> links = new ArrayList<>();

        links.add(linkTo(ConfigurationController.class).withSelfRel());
        links.add(linkTo(ConfigurationBricksController.class).withRel("bricks"));

        return new Resources<>(Collections.emptySet(), links);
    }
}
