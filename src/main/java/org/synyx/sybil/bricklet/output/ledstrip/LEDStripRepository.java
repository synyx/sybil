package org.synyx.sybil.bricklet.output.ledstrip;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.env.Environment;

import org.springframework.stereotype.Repository;

import org.synyx.sybil.LoadFailedException;
import org.synyx.sybil.bricklet.output.ledstrip.domain.LEDStrip;

import java.io.File;
import java.io.IOException;

import java.util.List;


/**
 * LEDStripRepository.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Repository
public class LEDStripRepository {

    private final ObjectMapper objectMapper;
    private final String configDir;

    @Autowired
    public LEDStripRepository(ObjectMapper objectMapper, Environment environment) {

        this.objectMapper = objectMapper;
        this.configDir = environment.getProperty("path.to.configfiles");
    }

    public LEDStrip get(String name) {

        for (LEDStrip ledStrip : getLedStrips()) {
            if (ledStrip.getName().equals(name)) {
                return ledStrip;
            }
        }

        return null;
    }


    public List<LEDStrip> getAll() {

        return getLedStrips();
    }


    private List<LEDStrip> getLedStrips() {

        List<LEDStrip> ledStrips;

        try {
            ledStrips = objectMapper.readValue(new File(configDir + "ledstrips.json"),
                    new TypeReference<List<LEDStrip>>() {
                    });
        } catch (IOException exception) {
            throw new LoadFailedException("Error loading LED strips config file:", exception);
        }

        return ledStrips;
    }
}
