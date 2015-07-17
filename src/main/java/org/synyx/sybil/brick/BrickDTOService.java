package org.synyx.sybil.brick;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.env.Environment;

import org.springframework.stereotype.Component;

import org.synyx.sybil.LoadFailedException;
import org.synyx.sybil.brick.domain.BrickDTO;
import org.synyx.sybil.brick.domain.BrickDomain;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;


/**
 * BrickDTOService.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Component
public class BrickDTOService {

    private final ObjectMapper objectMapper;
    private final String configDir;

    /**
     * Instantiates a new Brick DTO service.
     *
     * @param  objectMapper  the object mapper
     * @param  environment  the environment
     */
    @Autowired
    public BrickDTOService(ObjectMapper objectMapper, Environment environment) {

        this.objectMapper = objectMapper;
        this.configDir = environment.getProperty("path.to.configfiles");
    }

    /**
     * Gets a pre-configured DTO.
     *
     * @param  name  The name of the brick.
     *
     * @return  The DTO containing the brick's configuration.
     *
     * @throws  java.io.IOException  The IO exception.
     */
    public BrickDTO getDTO(String name) throws IOException {

        BrickDTO brickDTO = null;

        List<BrickDomain> brickDomains = objectMapper.readValue(new File(configDir + "bricks.json"),
                new TypeReference<List<BrickDomain>>() {
                });

        for (BrickDomain brickDomain : brickDomains) {
            if (brickDomain.getName().equals(name)) {
                brickDTO = new BrickDTO();
                brickDTO.setDomain(brickDomain);
            }
        }

        if (brickDTO == null) {
            throw new LoadFailedException("Brick " + name + " does not exist.");
        }

        return brickDTO;
    }


    /**
     * Gets pre-configured DTOs for all bricks.
     *
     * @return  A List of all the DTOs.
     *
     * @throws  java.io.IOException  The IO exception.
     */
    public List<BrickDTO> getAllDTOs() throws IOException {

        List<BrickDTO> brickDTOs = new ArrayList<>();

        List<BrickDomain> brickDomains = objectMapper.readValue(new File(configDir + "bricks.json"),
                new TypeReference<List<BrickDomain>>() {
                });

        for (BrickDomain brickDomain : brickDomains) {
            BrickDTO brickDTO;
            brickDTO = new BrickDTO();
            brickDTO.setDomain(brickDomain);
            brickDTOs.add(brickDTO);
        }

        return brickDTOs;
    }
}
