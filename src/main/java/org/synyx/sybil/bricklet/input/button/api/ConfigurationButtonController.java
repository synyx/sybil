package org.synyx.sybil.bricklet.input.button.api;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import org.synyx.sybil.bricklet.input.button.ButtonService;
import org.synyx.sybil.bricklet.input.button.database.ButtonDomain;

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

    private ButtonService buttonService;

    @Autowired
    public ConfigurationButtonController(ButtonService buttonService) {

        this.buttonService = buttonService;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = { "application/hal+json" })
    public Resources<ButtonResource> sensors() {

        List<ButtonDomain> buttons = buttonService.getAllDomains();
        List<ButtonResource> resources = new ArrayList<>();
        List<Link> links = new ArrayList<>();

        Link self = linkTo(ConfigurationButtonController.class).withSelfRel();
        links.add(self);

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

        ButtonDomain button = buttonService.getDomain(name);

        Link self = linkTo(methodOn(ConfigurationButtonController.class).sensor(button.getName())).withSelfRel();

        return new ButtonResource(button, self);
    }
}
