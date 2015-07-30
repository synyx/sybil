package org.synyx.sybil.bricklet.input.illuminance;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.env.Environment;

import org.springframework.stereotype.Service;

import org.synyx.sybil.LoadFailedException;
import org.synyx.sybil.bricklet.input.illuminance.domain.IlluminanceConfig;
import org.synyx.sybil.bricklet.input.illuminance.domain.IlluminanceDTO;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;


/**
 * IlluminanceDTOService.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Service
public class IlluminanceDTOService {

    private final ObjectMapper objectMapper;
    private final String configDir;

    /**
     * Instantiates a new illuminance sensor DTO service.
     *
     * @param  objectMapper  the object mapper
     * @param  environment  the environment
     */
    @Autowired
    public IlluminanceDTOService(ObjectMapper objectMapper, Environment environment) {

        this.objectMapper = objectMapper;
        this.configDir = environment.getProperty("path.to.configfiles");
    }

    /**
     * Gets a pre-configured DTO.
     *
     * @param  name  The name of the illuminance sensor.
     *
     * @return  The DTO containing the illuminance sensor's configuration.
     */
    public IlluminanceDTO getDTO(String name) {

        IlluminanceDTO illuminanceDTO = null;

        List<IlluminanceConfig> illuminanceConfigs = getIlluminanceDomains();

        for (IlluminanceConfig illuminanceConfig : illuminanceConfigs) {
            if (illuminanceConfig.getName().equals(name)) {
                illuminanceDTO = new IlluminanceDTO(illuminanceConfig);
            }
        }

        if (illuminanceDTO == null) {
            throw new IlluminanceNotFoundException("Illuminance sensor " + name + " does not exist.");
        }

        return illuminanceDTO;
    }


    /**
     * Gets pre-configured DTOs for all illuminance sensors.
     *
     * @return  A List of all the DTOs.
     */
    public List<IlluminanceDTO> getAllDTOs() {

        List<IlluminanceDTO> illuminanceDTOs = new ArrayList<>();

        List<IlluminanceConfig> illuminanceConfigs = getIlluminanceDomains();

        for (IlluminanceConfig illuminanceConfig : illuminanceConfigs) {
            illuminanceDTOs.add(new IlluminanceDTO(illuminanceConfig));
        }

        return illuminanceDTOs;
    }


    private List<IlluminanceConfig> getIlluminanceDomains() {

        try {
            return objectMapper.readValue(new File(configDir + "illuminances.json"),
                    new TypeReference<List<IlluminanceConfig>>() {
                    });
        } catch (IOException exception) {
            throw new LoadFailedException("Error loading illumunance sensor config file:", exception);
        }
    }
}
