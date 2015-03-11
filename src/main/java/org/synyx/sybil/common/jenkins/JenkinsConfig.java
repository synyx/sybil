package org.synyx.sybil.common.jenkins;

import org.springframework.stereotype.Component;

import org.synyx.sybil.out.StatusesOnLEDStrip;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * JenkinsConfig.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Component
public class JenkinsConfig {

    private Map<String, StatusesOnLEDStrip> mapping = new HashMap<>();

    public void put(String job, StatusesOnLEDStrip statusesOnLEDStrip) {

        mapping.put(job, statusesOnLEDStrip);
    }


    public boolean contains(String job) {

        return mapping.containsKey(job);
    }


    public StatusesOnLEDStrip get(String job) {

        return mapping.get(job);
    }


    public Collection<StatusesOnLEDStrip> getAll() {

        return mapping.values();
    }
}
