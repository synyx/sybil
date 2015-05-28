package org.synyx.sybil.bricklet.input.button.api;

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

import org.synyx.sybil.bricklet.input.button.database.ButtonDomain;
import org.synyx.sybil.bricklet.input.button.database.ButtonRepository;

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
@RequestMapping("/configuration/buttons")
public class ConfigurationButtonController {

    private ButtonRepository buttonRepository;
    private GraphDatabaseService graphDatabaseService;

    @Autowired
    public ConfigurationButtonController(ButtonRepository buttonRepository, GraphDatabaseService graphDatabaseService) {

        this.buttonRepository = buttonRepository;
        this.graphDatabaseService = graphDatabaseService;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = { "application/hal+json" })
    public Resources<ButtonResource> sensors() {

        List<ButtonDomain> buttons;
        List<ButtonResource> resources = new ArrayList<>();
        List<Link> links = new ArrayList<>();

        Link self = linkTo(ConfigurationButtonController.class).withSelfRel();
        links.add(self);

        try(Transaction tx = graphDatabaseService.beginTx()) { // begin transaction

            // get all sensors from database and cast them into a list so that they're actually fetched
            buttons = new ArrayList<>(IteratorUtil.asCollection(buttonRepository.findAll()));

            // end transaction
            tx.success();
        }

        for (ButtonDomain button : buttons) {
            self = linkTo(methodOn(ConfigurationButtonController.class).sensor(button.getName())).withSelfRel();

            ButtonResource resource = new ButtonResource(button, self);

            resources.add(resource);
        }

        return new Resources<>(resources, links);
    }


    @ResponseBody
    @RequestMapping(value = "/{name}", method = RequestMethod.GET, produces = { "application/hal+json" })
    public ButtonResource sensor(@PathVariable String name) {

        ButtonDomain button = buttonRepository.findByName(name);

        Link self = linkTo(methodOn(ConfigurationButtonController.class).sensor(button.getName())).withSelfRel();

        return new ButtonResource(button, self);
    }
}
