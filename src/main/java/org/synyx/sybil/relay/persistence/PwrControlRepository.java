package org.synyx.sybil.relay.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.env.Environment;

import org.springframework.stereotype.Repository;

import org.synyx.sybil.LoadFailedException;

import java.io.File;
import java.io.IOException;

import java.util.List;


/**
 * NetControlRepository.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Repository
public class PwrControlRepository {

    private final ObjectMapper objectMapper;
    private final String configFile;

    /**
     * Instantiates a new PwrCtrl DTO service.
     *
     * @param  objectMapper  the object mapper
     * @param  environment  the environment
     */
    @Autowired
    public PwrControlRepository(ObjectMapper objectMapper, Environment environment) {

        this.objectMapper = objectMapper;
        this.configFile = environment.getProperty("netcontrol.configfile");
    }

    public PwrControl get(String host) {

        for (PwrControl netcontrol : getNetControls()) {
            if (host.equals(netcontrol.getHost())) {
                return netcontrol;
            }
        }

        return null;
    }


    private List<PwrControl> getNetControls() {

        try {
            return objectMapper.readValue(new File(configFile), new TypeReference<List<PwrControl>>() {
                    });
        } catch (IOException exception) {
            throw new LoadFailedException("Error loading NetControls config file:", exception);
        }
    }
}
