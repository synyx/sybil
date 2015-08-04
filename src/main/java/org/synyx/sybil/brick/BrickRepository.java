package org.synyx.sybil.brick;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.env.Environment;

import org.springframework.stereotype.Component;

import org.synyx.sybil.LoadFailedException;
import org.synyx.sybil.brick.domain.Brick;

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
public class BrickRepository {

    private final ObjectMapper objectMapper;
    private final String configDir;

    @Autowired
    public BrickRepository(ObjectMapper objectMapper, Environment environment) {

        this.objectMapper = objectMapper;
        this.configDir = environment.getProperty("path.to.configfiles");
    }

    public Brick get(String name) {

        for (Brick brick : getBrickDomains()) {
            if (brick.getName().equals(name)) {
                return brick;
            }
        }

        return null;
    }


    public List<Brick> getAll() {

        List<Brick> bricks = new ArrayList<>();

        for (Brick brick : getBrickDomains()) {
            bricks.add(brick);
        }

        return bricks;
    }


    private List<Brick> getBrickDomains() {

        try {
            return objectMapper.readValue(new File(configDir + "bricks.json"), new TypeReference<List<Brick>>() {
                    });
        } catch (IOException exception) {
            throw new LoadFailedException("Error loading bricks config file:", exception);
        }
    }
}
