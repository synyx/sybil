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

import org.synyx.sybil.api.resources.SensorResource;
import org.synyx.sybil.database.InputSensorRepository;
import org.synyx.sybil.domain.InputSensorDomain;

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
@RequestMapping("/configuration/sensors")
public class ConfigurationSensorController {

    private InputSensorRepository inputSensorRepository;
    private GraphDatabaseService graphDatabaseService;

    @Autowired
    public ConfigurationSensorController(InputSensorRepository inputSensorRepository,
        GraphDatabaseService graphDatabaseService) {

        this.inputSensorRepository = inputSensorRepository;
        this.graphDatabaseService = graphDatabaseService;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = { "application/hal+json" })
    public Resources<SensorResource> sensors() {

        List<InputSensorDomain> sensors;
        List<SensorResource> resources = new ArrayList<>();
        List<Link> links = new ArrayList<>();

        Link self = linkTo(ConfigurationSensorController.class).withSelfRel();
        links.add(self);

        try(Transaction tx = graphDatabaseService.beginTx()) { // begin transaction

            // get all sensors from database and cast them into a list so that they're actually fetched
            sensors = new ArrayList<>(IteratorUtil.asCollection(inputSensorRepository.findAll()));

            // end transaction
            tx.success();
        }

        for (InputSensorDomain sensor : sensors) {
            self = linkTo(methodOn(ConfigurationSensorController.class).sensor(sensor.getName())).withSelfRel();

            SensorResource resource = new SensorResource(sensor, self);

            resources.add(resource);
        }

        return new Resources<>(resources, links);
    }


    @ResponseBody
    @RequestMapping(value = "/{name}", method = RequestMethod.GET, produces = { "application/hal+json" })
    public SensorResource sensor(@PathVariable String name) {

        InputSensorDomain sensor = inputSensorRepository.findByName(name);

        Link self = linkTo(methodOn(ConfigurationSensorController.class).sensor(sensor.getName())).withSelfRel();

        return new SensorResource(sensor, self);
    }
}
