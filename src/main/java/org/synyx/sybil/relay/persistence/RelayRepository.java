package org.synyx.sybil.relay.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.env.Environment;

import org.springframework.stereotype.Repository;

import org.synyx.sybil.LoadFailedException;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;


/**
 * RelayRepository.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Repository
public class RelayRepository {

    private final ObjectMapper objectMapper;
    private final String configDir;

    /**
     * Instantiates a new PwrCtrl DTO service.
     *
     * @param  objectMapper  the object mapper
     * @param  environment  the environment
     */
    @Autowired
    public RelayRepository(ObjectMapper objectMapper, Environment environment) {

        this.objectMapper = objectMapper;
        this.configDir = environment.getProperty("path.to.configfiles");
    }

    public Relay get(String name) {

        for (Relay relay : getRelays()) {
            if (name.equals(relay.getName())) {
                return relay;
            }
        }

        return null;
    }


    public List<Relay> getAll() {

        List<Relay> relays = new ArrayList<>();

        for (Relay relay : getRelays()) {
            relays.add(relay);
        }

        return relays;
    }


    private List<Relay> getRelays() {

        try {
            return objectMapper.readValue(new File(configDir + "relays.json"), new TypeReference<List<Relay>>() {
                    });
        } catch (IOException exception) {
            throw new LoadFailedException("Error loading Relays config file:", exception);
        }
    }
}
