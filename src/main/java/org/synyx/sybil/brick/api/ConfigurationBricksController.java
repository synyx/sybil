package org.synyx.sybil.brick.api;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import org.synyx.sybil.LoadFailedException;
import org.synyx.sybil.brick.BrickDTOService;
import org.synyx.sybil.brick.domain.BrickDTO;
import org.synyx.sybil.brick.domain.BrickDomain;

import java.io.IOException;

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

    private final BrickDTOService brickDTOService;

    @Autowired
    public ConfigurationBricksController(BrickDTOService brickDTOService) {

        this.brickDTOService = brickDTOService;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = { "application/hal+json" })
    public Resources<BrickResource> getBricks() {

        List<BrickDTO> brickDTOs;
        List<BrickResource> resources = new ArrayList<>();
        List<Link> links = new ArrayList<>();

        Link self = linkTo(ConfigurationBricksController.class).withSelfRel();
        links.add(self);

        try {
            brickDTOs = brickDTOService.getAllDTOs();
        } catch (IOException exception) {
            throw new LoadFailedException("Error loading bricks:", exception);
        }

        for (BrickDTO brickDTO : brickDTOs) {
            BrickDomain brickDomain = brickDTO.getDomain();

            self = linkTo(methodOn(ConfigurationBricksController.class).getBrick(brickDomain.getName())).withSelfRel();

            BrickResource resource = new BrickResource(brickDomain, self);

            resources.add(resource);
        }

        return new Resources<>(resources, links);
    }


    @ResponseBody
    @RequestMapping(value = "/{name}", method = RequestMethod.GET, produces = { "application/hal+json" })
    public BrickResource getBrick(@PathVariable String name) {

        BrickDomain brickDomain;

        try {
            brickDomain = brickDTOService.getDTO(name).getDomain();
        } catch (IOException | NullPointerException exception) {
            throw new LoadFailedException("Error loading brick:", exception);
        }

        Link self = linkTo(methodOn(ConfigurationBricksController.class).getBrick(brickDomain.getName())).withSelfRel();

        return new BrickResource(brickDomain, self);
    }
}
