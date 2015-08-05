package org.synyx.sybil.bricklet.input.illuminance;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.env.Environment;

import org.springframework.stereotype.Repository;

import org.synyx.sybil.LoadFailedException;
import org.synyx.sybil.bricklet.input.illuminance.domain.Illuminance;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;


/**
 * IlluminanceRepository.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Repository
public class IlluminanceRepository {

    private final ObjectMapper objectMapper;
    private final String configDir;

    /**
     * Instantiates a new illuminance sensor DTO service.
     *
     * @param  objectMapper  the object mapper
     * @param  environment  the environment
     */
    @Autowired
    public IlluminanceRepository(ObjectMapper objectMapper, Environment environment) {

        this.objectMapper = objectMapper;
        this.configDir = environment.getProperty("path.to.configfiles");
    }

    public Illuminance get(String name) {

        for (Illuminance illuminance : getIlluminances()) {
            if (illuminance.getName().equals(name)) {
                return illuminance;
            }
        }

        return null;
    }


    public List<Illuminance> getAll() {

        List<Illuminance> illuminances = new ArrayList<>();

        for (Illuminance illuminance : getIlluminances()) {
            illuminances.add(illuminance);
        }

        return illuminances;
    }


    private List<Illuminance> getIlluminances() {

        try {
            return objectMapper.readValue(new File(configDir + "illuminances.json"),
                    new TypeReference<List<Illuminance>>() {
                    });
        } catch (IOException exception) {
            throw new LoadFailedException("Error loading illumunance sensor config file:", exception);
        }
    }
}
