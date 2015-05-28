package org.synyx.sybil.jenkins.config;

import org.apache.commons.codec.binary.Base64;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import org.springframework.stereotype.Component;

import org.synyx.sybil.bricklet.output.ledstrip.SingleStatusOnLEDStrip;
import org.synyx.sybil.jenkins.domain.JenkinsProperties;

import java.nio.charset.Charset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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

    /**
     * Map <Servername, Map<Jobname, List<SingleStatusOnLEDStrip>>>.
     */
    private Map<String, Map<String, List<SingleStatusOnLEDStrip>>> mapping = new HashMap<>();

    /**
     * Maps Servers to their authentication entities.
     */
    private Map<String, HttpEntity<JenkinsProperties[]>> servers = new HashMap<>();

    /**
     * Put server into map, creating Authentication entity.
     *
     * @param  server  the server
     * @param  username  the username
     * @param  key  the key
     */
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


    /**
     * Gets Authentication entity for server.
     *
     * @param  server  the server
     *
     * @return  the server
     */
    public HttpEntity<JenkinsProperties[]> getServer(String server) {

        return servers.get(server);
    }


    /**
     * Gets List of all configured servers.
     *
     * @return  the servers
     */
    public Set<String> getServers() {

        return servers.keySet();
    }


    /**
     * Put a SingleStatusOnLEDStrip into map, referenced by the server and the job.
     *
     * @param  server  the server
     * @param  job  the job
     * @param  singleStatusOnLEDStrip  the single status on lED strip
     */
    public void put(String server, String job, SingleStatusOnLEDStrip singleStatusOnLEDStrip) {

        Map<String, List<SingleStatusOnLEDStrip>> jobs = mapping.get(server);

        if (jobs == null) {
            jobs = new HashMap<>();
        }

        List<SingleStatusOnLEDStrip> singleStatusOnLEDStrips = jobs.get(job);

        if (singleStatusOnLEDStrips == null) {
            singleStatusOnLEDStrips = new ArrayList<>();
        }

        singleStatusOnLEDStrips.add(singleStatusOnLEDStrip);

        jobs.put(job, singleStatusOnLEDStrips);

        mapping.put(server, jobs);
    }


    /**
     * Contains boolean.
     *
     * @param  server  the server
     * @param  job  the job
     *
     * @return  the boolean
     */
    public boolean contains(String server, String job) {

        boolean containsKey = false;

        if (mapping.containsKey(server)) {
            containsKey = mapping.get(server).containsKey(job);
        }

        return containsKey;
    }


    /**
     * Get list.
     *
     * @param  server  the server
     * @param  job  the job
     *
     * @return  the list
     */
    public List<SingleStatusOnLEDStrip> get(String server, String job) {

        if (mapping.containsKey(server)) {
            return mapping.get(server).get(job);
        } else {
            return null;
        }
    }


    /**
     * Get map.
     *
     * @param  server  the server
     *
     * @return  the map
     */
    public Map<String, List<SingleStatusOnLEDStrip>> get(String server) {

        return mapping.get(server);
    }


    /**
     * Gets all.
     *
     * @return  the all
     */
    public Set<SingleStatusOnLEDStrip> getAll() {

        Set<SingleStatusOnLEDStrip> result = new HashSet<>();

        for (Map<String, List<SingleStatusOnLEDStrip>> jobs : mapping.values()) {
            for (List<SingleStatusOnLEDStrip> ledStrips : jobs.values()) {
                result.addAll(ledStrips);
            }
        }

        return result;
    }


    /**
     * Reset void.
     */
    public void reset() {

        mapping.clear();
    }
}
