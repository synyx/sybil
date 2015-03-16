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

import org.synyx.sybil.api.resources.LEDStripResource;
import org.synyx.sybil.database.OutputLEDStripRepository;
import org.synyx.sybil.domain.OutputLEDStripDomain;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;


/**
 * ConfigurationLEDStripsController.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@RestController
@RequestMapping("/configuration/ledstrips")
public class ConfigurationLEDStripController {

    private OutputLEDStripRepository outputLEDStripRepository;
    private GraphDatabaseService graphDatabaseService;

    @Autowired
    public ConfigurationLEDStripController(OutputLEDStripRepository outputLEDStripRepository,
        GraphDatabaseService graphDatabaseService) {

        this.outputLEDStripRepository = outputLEDStripRepository;
        this.graphDatabaseService = graphDatabaseService;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = { "application/hal+json" })
    public Resources<LEDStripResource> bricks() {

        List<OutputLEDStripDomain> ledStrips;
        List<LEDStripResource> resources = new ArrayList<>();
        List<Link> links = new ArrayList<>();

        Link self = linkTo(ConfigurationLEDStripController.class).withSelfRel();
        links.add(self);

        try(Transaction tx = graphDatabaseService.beginTx()) { // begin transaction

            // get all Bricks from database and cast them into a list so that they're actually fetched
            ledStrips = new ArrayList<>(IteratorUtil.asCollection(outputLEDStripRepository.findAll()));

            // end transaction
            tx.success();
        }

        for (OutputLEDStripDomain ledStripDomain : ledStrips) {
            self = linkTo(methodOn(ConfigurationLEDStripController.class).ledStrip(ledStripDomain.getName()))
                .withSelfRel();

            LEDStripResource resource = new LEDStripResource(ledStripDomain, self);

            resources.add(resource);
        }

        return new Resources<>(resources, links);
    }


    @ResponseBody
    @RequestMapping(value = "/{name}", method = RequestMethod.GET, produces = { "application/hal+json" })
    public LEDStripResource ledStrip(@PathVariable String name) {

        OutputLEDStripDomain ledStripDomain = outputLEDStripRepository.findByName(name);

        Link self = linkTo(methodOn(ConfigurationLEDStripController.class).ledStrip(ledStripDomain.getName()))
            .withSelfRel();

        return new LEDStripResource(ledStripDomain, self);
    }
}
