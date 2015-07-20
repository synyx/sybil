package org.synyx.sybil.bricklet.input.illuminance;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.env.Environment;

import org.springframework.stereotype.Service;

import org.synyx.sybil.LoadFailedException;
import org.synyx.sybil.bricklet.input.illuminance.domain.IlluminanceDTO;
import org.synyx.sybil.bricklet.input.illuminance.domain.IlluminanceDomain;

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
     *
     * @throws  java.io.IOException  The IO exception.
     */
    public IlluminanceDTO getDTO(String name) throws IOException {

        IlluminanceDTO illuminanceDTO = null;

        List<IlluminanceDomain> illuminanceDomains = objectMapper.readValue(new File(configDir + "illuminances.json"),
                new TypeReference<List<IlluminanceDomain>>() {
                });

        for (IlluminanceDomain ledStripDomain : illuminanceDomains) {
            if (ledStripDomain.getName().equals(name)) {
                illuminanceDTO = new IlluminanceDTO();
                illuminanceDTO.setDomain(ledStripDomain);
            }
        }

        if (illuminanceDTO == null) {
            throw new LoadFailedException("Illuminance sensor " + name + " does not exist.");
        }

        return illuminanceDTO;
    }


    /**
     * Gets pre-configured DTOs for all illuminance sensors.
     *
     * @return  A List of all the DTOs.
     *
     * @throws  IOException  The IO exception.
     */
    public List<IlluminanceDTO> getAllDTOs() throws IOException {

        List<IlluminanceDTO> illuminanceDTOs = new ArrayList<>();

        List<IlluminanceDomain> illuminanceDomains = objectMapper.readValue(new File(configDir + "illuminances.json"),
                new TypeReference<List<IlluminanceDomain>>() {
                });

        for (IlluminanceDomain illuminanceDomain : illuminanceDomains) {
            IlluminanceDTO ledStripDTO;
            ledStripDTO = new IlluminanceDTO();
            ledStripDTO.setDomain(illuminanceDomain);
            illuminanceDTOs.add(ledStripDTO);
        }

        return illuminanceDTOs;
    }
}
