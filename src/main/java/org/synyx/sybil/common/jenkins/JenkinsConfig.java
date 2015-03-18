package org.synyx.sybil.common.jenkins;

import org.apache.commons.codec.binary.Base64;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import org.springframework.stereotype.Component;

import org.synyx.sybil.out.SingleStatusOnLEDStrip;

import java.nio.charset.Charset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * JenkinsConfig.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Component
public class JenkinsConfig {

    private Map<String, Map<String, List<SingleStatusOnLEDStrip>>> mapping = new HashMap<>();

    private Map<String, HttpEntity<JenkinsProperties[]>> servers = new HashMap<>();

    public void putServer(String server, String username, String key) {

        // HTTP Basic Authorization, as demanded by the Jenkins API.

        HttpHeaders headers = new HttpHeaders(); // HTTP header

        // generate Basic Auth header
        headers.set("Authorization",
            "Basic "
            + new String(Base64.encodeBase64((username + ":" + key).getBytes(Charset.forName("US-ASCII")))));

        // create HttpEntity with Auth header, which can then be used with Spring REST Template
        HttpEntity<JenkinsProperties[]> requestEntity = new HttpEntity<>(headers);

        servers.put(server, requestEntity);
    }


    public HttpEntity<JenkinsProperties[]> getServer(String server) {

        return servers.get(server);
    }


    public Set<String> getServers() {

        return servers.keySet();
    }


    public void put(String server, String job, SingleStatusOnLEDStrip singleStatusOnLEDStrip) {

        Map<String, List<SingleStatusOnLEDStrip>> jobs = mapping.get(server);

        if (jobs == null) {
            jobs = new HashMap<>();
        }

        List<SingleStatusOnLEDStrip> statuses = jobs.get(job);

        if (statuses == null) {
            statuses = new ArrayList<>();
        }

        statuses.add(singleStatusOnLEDStrip);

        jobs.put(job, statuses);

        mapping.put(server, jobs);
    }


    public boolean contains(String server, String job) {

        if (mapping.containsKey(server)) {
            return mapping.get(server).containsKey(job);
        } else {
            return false;
        }
    }


    public List<SingleStatusOnLEDStrip> get(String server, String job) {

        if (mapping.containsKey(server)) {
            return mapping.get(server).get(job);
        } else {
            return null;
        }
    }


    public Map<String, List<SingleStatusOnLEDStrip>> get(String server) {

        return mapping.get(server);
    }


    public Collection<List<SingleStatusOnLEDStrip>> getAll(String server) {

        if (mapping.containsKey(server)) {
            return mapping.get(server).values();
        } else {
            return null;
        }
    }


    public void reset() {

        mapping.clear();
    }
}
