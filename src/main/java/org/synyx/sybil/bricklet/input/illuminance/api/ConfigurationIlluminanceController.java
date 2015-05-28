package org.synyx.sybil.bricklet.input.illuminance.api;

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

import org.synyx.sybil.bricklet.input.illuminance.database.IlluminanceSensorDomain;
import org.synyx.sybil.bricklet.input.illuminance.database.IlluminanceSensorRepository;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;


/**
 * ConfigurationSensorController.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@RestController
@RequestMapping("/configuration/illuminancesensors")
public class ConfigurationIlluminanceController {

    private IlluminanceSensorRepository illuminanceSensorRepository;
    private GraphDatabaseService graphDatabaseService;

    @Autowired
    public ConfigurationIlluminanceController(IlluminanceSensorRepository illuminanceSensorRepository,
        GraphDatabaseService graphDatabaseService) {

        this.illuminanceSensorRepository = illuminanceSensorRepository;
        this.graphDatabaseService = graphDatabaseService;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = { "application/hal+json" })
    public Resources<IlluminanceResource> sensors() {

        List<IlluminanceSensorDomain> sensors;
        List<IlluminanceResource> resources = new ArrayList<>();
        List<Link> links = new ArrayList<>();

        Link self = linkTo(ConfigurationIlluminanceController.class).withSelfRel();
        links.add(self);

        try(Transaction tx = graphDatabaseService.beginTx()) { // begin transaction

            // get all sensors from database and cast them into a list so that they're actually fetched
            sensors = new ArrayList<>(IteratorUtil.asCollection(illuminanceSensorRepository.findAll()));

            // end transaction
            tx.success();
        }

        for (IlluminanceSensorDomain sensor : sensors) {
            self = linkTo(methodOn(ConfigurationIlluminanceController.class).sensor(sensor.getName())).withSelfRel();

            IlluminanceResource resource = new IlluminanceResource(sensor, self);

            resources.add(resource);
        }

        return new Resources<>(resources, links);
    }


    @ResponseBody
    @RequestMapping(value = "/{name}", method = RequestMethod.GET, produces = { "application/hal+json" })
    public IlluminanceResource sensor(@PathVariable String name) {

        IlluminanceSensorDomain sensor = illuminanceSensorRepository.findByName(name);

        Link self = linkTo(methodOn(ConfigurationIlluminanceController.class).sensor(sensor.getName())).withSelfRel();

        return new IlluminanceResource(sensor, self);
    }
}
