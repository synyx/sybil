package org.synyx.sybil.bricklet.output.ledstrip;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.env.Environment;

import org.springframework.stereotype.Component;

import org.synyx.sybil.LoadFailedException;
import org.synyx.sybil.bricklet.output.ledstrip.database.LEDStripDomain;
import org.synyx.sybil.bricklet.output.ledstrip.domain.LEDStripDTO;

import java.io.File;
import java.io.IOException;

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
    public LEDStripDTO getDTO(String name) throws IOException {

        LEDStripDTO ledStripDTO = null;

        List<LEDStripDomain> ledStripDomains = objectMapper.readValue(new File(configDir + "ledstrips.json"),
                new TypeReference<List<LEDStripDomain>>() {
                });

        for (LEDStripDomain ledStripDomain : ledStripDomains) {
            if (ledStripDomain.getName().equals(name)) {
                ledStripDTO = new LEDStripDTO();
                ledStripDTO.setDomain(ledStripDomain);
            }
        }

        if (ledStripDTO == null) {
            throw new LoadFailedException("LED strip " + name + " does not exist.");
        }

        return ledStripDTO;
    }
}
