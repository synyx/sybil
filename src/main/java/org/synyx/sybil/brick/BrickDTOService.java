package org.synyx.sybil.brick;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.tinkerforge.IPConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.env.Environment;

import org.springframework.stereotype.Component;

import org.synyx.sybil.LoadFailedException;
import org.synyx.sybil.brick.domain.BrickConfig;
import org.synyx.sybil.brick.domain.BrickDTO;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;


/**
 * BrickDTOService.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Component
public class BrickDTOService {

    private static final Logger LOG = LoggerFactory.getLogger(BrickDTOService.class);

    private final ObjectMapper objectMapper;
    private final String configDir;
    private final BrickService brickService;

    /**
     * Instantiates a new Brick DTO service.
     *
     * @param  objectMapper  the object mapper
     * @param  environment  the environment
     * @param  brickService  the Brick service
     */
    @Autowired
    public BrickDTOService(ObjectMapper objectMapper, Environment environment, BrickService brickService) {

        this.objectMapper = objectMapper;
        this.brickService = brickService;
        this.configDir = environment.getProperty("path.to.configfiles");
    }

    @PostConstruct
    public void resetAllBricks() {

        try {
            List<BrickDTO> brickDTOs = getAllDTOs();

            for (BrickDTO brickDTO : brickDTOs) {
                brickService.reset(brickDTO);
            }
        } catch (BrickConnectionException | LoadFailedException exception) {
            LOG.error("Failed to reset bricks:", exception);
        }
    }


    public IPConnection connect(String name) {

        return brickService.connect(getDTO(name));
    }


    /**
     * Gets a pre-configured DTO.
     *
     * @param  name  The name of the brick.
     *
     * @return  The DTO containing the brick's configuration.
     */
    private BrickDTO getDTO(String name) {

        BrickDTO brickDTO = null;

        List<BrickConfig> brickConfigs = getBrickDomains();

        for (BrickConfig brickConfig : brickConfigs) {
            if (brickConfig.getName().equals(name)) {
                brickDTO = new BrickDTO(brickConfig);
            }
        }

        if (brickDTO == null) {
            throw new BrickNotFoundException("Brick " + name + " does not exist.");
        }

        return brickDTO;
    }


    /**
     * Gets pre-configured DTOs for all bricks.
     *
     * @return  A List of all the DTOs.
     */
    private List<BrickDTO> getAllDTOs() {

        List<BrickDTO> brickDTOs = new ArrayList<>();

        List<BrickConfig> brickConfigs = getBrickDomains();

        for (BrickConfig brickConfig : brickConfigs) {
            brickDTOs.add(new BrickDTO(brickConfig));
        }

        return brickDTOs;
    }


    private List<BrickConfig> getBrickDomains() {

        try {
            return objectMapper.readValue(new File(configDir + "bricks.json"),
                    new TypeReference<List<BrickConfig>>() {
                    });
        } catch (IOException exception) {
            throw new LoadFailedException("Error loading bricks config file:", exception);
        }
    }
}
