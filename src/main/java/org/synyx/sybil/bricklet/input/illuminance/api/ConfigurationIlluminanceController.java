package org.synyx.sybil.bricklet.input.illuminance.api;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import org.synyx.sybil.bricklet.input.illuminance.IlluminanceService;
import org.synyx.sybil.bricklet.input.illuminance.database.IlluminanceSensorDomain;

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

    private IlluminanceService illuminanceService;

    @Autowired
    public ConfigurationIlluminanceController(IlluminanceService illuminanceService) {

        this.illuminanceService = illuminanceService;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = { "application/hal+json" })
    public Resources<IlluminanceResource> sensors() {

        List<IlluminanceSensorDomain> sensors = illuminanceService.getAllDomains();
        List<IlluminanceResource> resources = new ArrayList<>();
        List<Link> links = new ArrayList<>();

        Link self = linkTo(ConfigurationIlluminanceController.class).withSelfRel();
        links.add(self);

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

        IlluminanceSensorDomain sensor = illuminanceService.getDomain(name);

        Link self = linkTo(methodOn(ConfigurationIlluminanceController.class).sensor(sensor.getName())).withSelfRel();

        return new IlluminanceResource(sensor, self);
    }
}
