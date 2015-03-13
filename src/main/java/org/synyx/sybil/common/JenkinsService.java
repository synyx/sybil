package org.synyx.sybil.common;

import org.apache.commons.codec.binary.Base64;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.env.Environment;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.stereotype.Service;

import org.springframework.web.client.RestTemplate;

import org.synyx.sybil.common.jenkins.JenkinsConfig;
import org.synyx.sybil.common.jenkins.JenkinsJob;
import org.synyx.sybil.common.jenkins.JenkinsProperties;
import org.synyx.sybil.in.Status;
import org.synyx.sybil.in.StatusInformation;
import org.synyx.sybil.out.SingleStatusOnLEDStrip;

import java.nio.charset.Charset;

import java.util.Collection;
import java.util.List;

import javax.annotation.PreDestroy;


/**
 * JenkinsService. Reads the Jenkins API in regular intervals, compares it to the Jenkins build alert configuration.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Service
public class JenkinsService {

    /**
     * The Rest.
     */
    RestTemplate rest = new RestTemplate();

    /**
     * The Headers.
     */
    HttpHeaders headers = new HttpHeaders();

    /**
     * The Request entity.
     */
    HttpEntity<JenkinsProperties[]> requestEntity;

    /**
     * The Jobs.
     */
    JenkinsJob[] jobs;

    /**
     * The Jenkins config.
     */
    JenkinsConfig jenkinsConfig;

    /**
     * The Jenkins uRL.
     */
    String jenkinsURL;

    /**
     * Instantiates a new Jenkins service.
     *
     * @param  jenkinsConfig  the jenkins config
     * @param  env  the env
     */
    @Autowired
    public JenkinsService(JenkinsConfig jenkinsConfig, Environment env) {

        jenkinsURL = env.getProperty("jenkins.url");

        String jenkinsUsername = env.getProperty("jenkins.user");
        String jenkinsKey = env.getProperty("jenkins.key");

        // HTTP Basic Authorization, as demanded by the Jenkins API.
        headers.set("Authorization",
            "Basic "
            + new String(
                Base64.encodeBase64((jenkinsUsername + ":" + jenkinsKey).getBytes(Charset.forName("US-ASCII")))));

        requestEntity = new HttpEntity<>(headers);

        this.jenkinsConfig = jenkinsConfig;
    }

    private JenkinsProperties retrieveJobs() {

        ResponseEntity<JenkinsProperties> response = rest.exchange(jenkinsURL, HttpMethod.GET, requestEntity,
                JenkinsProperties.class);

        return response.getBody();
    }


    private void updateStatus(List<SingleStatusOnLEDStrip> ledStrips, String jobName, String status) {

        StatusInformation statusInformation = null;

        switch (status) {
            case "red":
                statusInformation = new StatusInformation(jobName, Status.CRITICAL);
                break;

            case "yellow":
                statusInformation = new StatusInformation(jobName, Status.WARNING);
                break;

            case "blue":
                statusInformation = new StatusInformation(jobName, Status.OKAY);
        }

        if (statusInformation != null) {
            for (SingleStatusOnLEDStrip ledStrip : ledStrips) {
                if (ledStrip.getStatus().ordinal() < statusInformation.getStatus().ordinal())
                    ledStrip.setStatus(statusInformation);
            }
        }
    }


    /**
     * Destroy void.
     */
    @PreDestroy
    public void destroy() {

        Collection<List<SingleStatusOnLEDStrip>> allLEDStrips = jenkinsConfig.getAll();

        for (List<SingleStatusOnLEDStrip> ledStripList : allLEDStrips) {
            for (SingleStatusOnLEDStrip ledStrip : ledStripList) {
                ledStrip.turnOff();
            }
        }
    }


    /**
     * Clear all statuses on SingleStatusOnLEDStrips, so priority sorting can commence.
     */
    private void clearLEDStripStatuses() {

        Collection<List<SingleStatusOnLEDStrip>> allLEDStrips = jenkinsConfig.getAll();

        for (List<SingleStatusOnLEDStrip> ledStripList : allLEDStrips) {
            for (SingleStatusOnLEDStrip ledStrip : ledStripList) {
                ledStrip.setStatus(new StatusInformation("Clear", Status.OKAY));
            }
        }
    }


    private void showStatuses() {

        Collection<List<SingleStatusOnLEDStrip>> allLEDStrips = jenkinsConfig.getAll();

        for (List<SingleStatusOnLEDStrip> ledStripList : allLEDStrips) {
            for (SingleStatusOnLEDStrip ledStrip : ledStripList) {
                ledStrip.showStatus();
            }
        }
    }


    /**
     * Handle jobs.
     */
    @Scheduled(fixedRate = 15000)
    public void handleJobs() {

        jobs = retrieveJobs().getJobs();

        clearLEDStripStatuses();

        for (JenkinsJob job : jobs) {
            if (jenkinsConfig.contains(job.getName())) {
                updateStatus(jenkinsConfig.get(job.getName()), job.getName(), job.getColor());
            }
        }

        showStatuses();
    }
}
