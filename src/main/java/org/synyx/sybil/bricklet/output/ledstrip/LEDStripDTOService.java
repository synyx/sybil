package org.synyx.sybil.bricklet.output.ledstrip;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.env.Environment;

import org.springframework.stereotype.Component;

import org.synyx.sybil.LoadFailedException;
import org.synyx.sybil.bricklet.output.ledstrip.domain.LEDStripDTO;
import org.synyx.sybil.bricklet.output.ledstrip.domain.LEDStripDomain;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;


/**
 * LEDStripDTOService.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Component
public class LEDStripDTOService {

    private final ObjectMapper objectMapper;
    private final String configDir;

    /**
     * Instantiates a new LED strip DTO service.
     *
     * @param  objectMapper  the object mapper
     * @param  environment  the environment
     */
    @Autowired
    public LEDStripDTOService(ObjectMapper objectMapper, Environment environment) {

        this.objectMapper = objectMapper;
        this.configDir = environment.getProperty("path.to.configfiles");
    }

    /**
     * Gets a pre-configured DTO.
     *
     * @param  name  The name of the LED strip.
     *
     * @return  The DTO containing the LED strip's configuration.
     */
    public LEDStripDTO getDTO(String name) {

        LEDStripDTO ledStripDTO = null;

        for (LEDStripDomain ledStripDomain : getLedStripDomains()) {
            if (ledStripDomain.getName().equals(name)) {
                ledStripDTO = new LEDStripDTO(ledStripDomain);
            }
        }

        if (ledStripDTO == null) {
            throw new LEDStripNotFoundException("LED strip " + name + " is not configured.");
        }

        return ledStripDTO;
    }


    /**
     * Gets pre-configured DTOs for all LED strips.
     *
     * @return  A List of all the DTOs.
     */
    public List<LEDStripDTO> getAllDTOs() {

        List<LEDStripDTO> ledStripDTOs = new ArrayList<>();

        for (LEDStripDomain ledStripDomain : getLedStripDomains()) {
            ledStripDTOs.add(new LEDStripDTO(ledStripDomain));
        }

        return ledStripDTOs;
    }


    private List<LEDStripDomain> getLedStripDomains() {

        List<LEDStripDomain> ledStripDomains;

        try {
            ledStripDomains = objectMapper.readValue(new File(configDir + "ledstrips.json"),
                    new TypeReference<List<LEDStripDomain>>() {
                    });
        } catch (IOException exception) {
            throw new LoadFailedException("Error loading LED strips config file:", exception);
        }

        return ledStripDomains;
    }
}
