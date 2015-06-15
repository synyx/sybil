package org.synyx.sybil.brick.api;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import org.synyx.sybil.brick.BrickService;
import org.synyx.sybil.brick.database.BrickDomain;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;


/**
 * ConfigurationBricksController.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@RestController
@RequestMapping("/configuration/bricks")
public class ConfigurationBricksController {

    private BrickService brickService;

    @Autowired
    public ConfigurationBricksController(BrickService brickService) {

        this.brickService = brickService;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = { "application/hal+json" })
    public Resources<BrickResource> bricks() {

        List<BrickDomain> bricks = brickService.getAllBrickDomains();
        List<BrickResource> resources = new ArrayList<>();
        List<Link> links = new ArrayList<>();

        Link self = linkTo(ConfigurationBricksController.class).withSelfRel();
        links.add(self);

        for (BrickDomain brick : bricks) {
            self = linkTo(methodOn(ConfigurationBricksController.class).brick(brick.getName())).withSelfRel();

            BrickResource resource = new BrickResource(brick, self);

            resources.add(resource);
        }

        return new Resources<>(resources, links);
    }


    @ResponseBody
    @RequestMapping(value = "/{name}", method = RequestMethod.GET, produces = { "application/hal+json" })
    public BrickResource brick(@PathVariable String name) {

        BrickDomain brick = brickService.getBrickDomain(name);

        Link self = linkTo(methodOn(ConfigurationBricksController.class).brick(brick.getName())).withSelfRel();

        return new BrickResource(brick, self);
    }
}
