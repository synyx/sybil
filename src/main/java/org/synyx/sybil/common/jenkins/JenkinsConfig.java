package org.synyx.sybil.common.jenkins;

import org.springframework.stereotype.Component;

import org.synyx.sybil.out.SingleStatusOnLEDStrip;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * JenkinsConfig.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Component
public class JenkinsConfig {

    private Map<String, List<SingleStatusOnLEDStrip>> mapping = new HashMap<>();

    public void put(String job, SingleStatusOnLEDStrip singleStatusOnLEDStrip) {

        List<SingleStatusOnLEDStrip> statuses = mapping.get(job);

        if (statuses == null) {
            statuses = new ArrayList<>();
        }

        statuses.add(singleStatusOnLEDStrip);

        mapping.put(job, statuses);
    }


    public boolean contains(String job) {

        return mapping.containsKey(job);
    }


    public List<SingleStatusOnLEDStrip> get(String job) {

        return mapping.get(job);
    }


    public Collection<List<SingleStatusOnLEDStrip>> getAll() {

        return mapping.values();
    }


    public void reset() {

        mapping.clear();
    }
}
