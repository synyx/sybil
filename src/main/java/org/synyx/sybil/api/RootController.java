package org.synyx.sybil.api;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("/")
public class RootController {

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public Resources<Object> root() {

        List<Link> links = new ArrayList<>();

        links.add(linkTo(RootController.class).withSelfRel());
        links.add(linkTo(ConfigurationController.class).withRel("configuration"));

        return new Resources<>(Collections.emptySet(), links);
    }
}