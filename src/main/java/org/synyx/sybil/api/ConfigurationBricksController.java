package org.synyx.sybil.api;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

import org.neo4j.helpers.collection.IteratorUtil;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import org.synyx.sybil.api.resources.BrickResource;
import org.synyx.sybil.database.BrickRepository;
import org.synyx.sybil.domain.BrickDomain;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;


/**
 * ConfigurationController.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@RestController
@RequestMapping("/configuration/bricks")
public class ConfigurationBricksController {

    private BrickRepository brickRepository;
    private GraphDatabaseService graphDatabaseService;

    @Autowired
    public ConfigurationBricksController(BrickRepository brickRepository, GraphDatabaseService graphDatabaseService) {

        this.brickRepository = brickRepository;
        this.graphDatabaseService = graphDatabaseService;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public Resources<BrickResource> bricks() {

        List<BrickDomain> bricks;
        List<BrickResource> resources = new ArrayList<>();
        List<Link> links = new ArrayList<>();

        Link self = linkTo(ConfigurationBricksController.class).withSelfRel();
        links.add(self);

        try(Transaction tx = graphDatabaseService.beginTx()) { // begin transaction

            // get all Bricks from database and cast them into a list so that they're actually fetched
            bricks = new ArrayList<>(IteratorUtil.asCollection(brickRepository.findAll()));

            // end transaction
            tx.success();
        }

        for (BrickDomain brick : bricks) {
            self = linkTo(methodOn(ConfigurationBricksController.class).brick(brick.getHostname())).withSelfRel();

            BrickResource resource = new BrickResource(brick, self);

            resources.add(resource);
        }

        return new Resources<>(resources, links);
    }


    @ResponseBody
    @RequestMapping(value = "/{hostname}", method = RequestMethod.GET)
    public BrickResource brick(@PathVariable String hostname) {

        BrickDomain brick = brickRepository.findByHostname(hostname);

        Link self = linkTo(methodOn(ConfigurationBricksController.class).brick(brick.getHostname())).withSelfRel();
        BrickResource resource = new BrickResource(brick, self);

        return resource;
    }
}
