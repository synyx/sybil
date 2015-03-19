package org.synyx.sybil.in;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.stereotype.Service;

import org.springframework.web.client.RestTemplate;

import org.synyx.sybil.common.jenkins.JenkinsConfig;
import org.synyx.sybil.common.jenkins.JenkinsJob;
import org.synyx.sybil.common.jenkins.JenkinsProperties;
import org.synyx.sybil.out.SingleStatusOnLEDStrip;

import java.util.List;
import java.util.Set;

import javax.annotation.PreDestroy;


/**
 * JenkinsService. Reads the Jenkins API in regular intervals, compares it to the Jenkins build alert configuration.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Service
public class JenkinsService {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(JenkinsService.class);

    /**
     * Spring REST Template.
     */
    private final RestTemplate rest = new RestTemplate();

    /**
     * The Jenkins config object, contains all the configured servers and jobs.
     */
    private final JenkinsConfig jenkinsConfig;

    /**
     * Instantiates a new Jenkins service.
     *
     * @param  jenkinsConfig  the jenkins config bean
     */
    @Autowired
    public JenkinsService(JenkinsConfig jenkinsConfig) {

        this.jenkinsConfig = jenkinsConfig;
    }

    private JenkinsProperties retrieveJobs(String server) {

        HttpEntity<JenkinsProperties[]> requestEntity = jenkinsConfig.getServer(server);

        try {
            ResponseEntity<JenkinsProperties> response = rest.exchange(server + "/api/json", HttpMethod.GET,
                    requestEntity, JenkinsProperties.class);

            return response.getBody();
        } catch (Exception e) {
            LOG.error(server + ": " + e.getMessage());

            return null;
        }
    }


    private void updateStatus(List<SingleStatusOnLEDStrip> ledStrips, String jobName, String status) {

        StatusInformation statusInformation = null;

        switch (status) {
            case "red":
            case "red_anime":
                statusInformation = new StatusInformation(jobName, Status.CRITICAL);
                break;

            case "yellow":
            case "yellow_anime":
                statusInformation = new StatusInformation(jobName, Status.WARNING);
                break;

            case "blue":
            case "blue_anime":
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

        Set<SingleStatusOnLEDStrip> allLEDStrips = jenkinsConfig.getAll();

        for (SingleStatusOnLEDStrip ledStrip : allLEDStrips) {
            ledStrip.turnOff();
        }
    }


    /**
     * Clear all statuses on SingleStatusOnLEDStrips, so priority sorting can commence.
     */
    private void clearLEDStripStatuses() {

        Set<SingleStatusOnLEDStrip> allLEDStrips = jenkinsConfig.getAll();

        if (allLEDStrips != null) {
            for (SingleStatusOnLEDStrip ledStrip : allLEDStrips) {
                ledStrip.setStatus(new StatusInformation("Clear", Status.OKAY));
            }
        } else {
            LOG.error("No LED Strips configured.");
        }
    }


    private void showStatuses() {

        Set<SingleStatusOnLEDStrip> allLEDStrips = jenkinsConfig.getAll();

        if (allLEDStrips != null) {
            for (SingleStatusOnLEDStrip ledStrip : allLEDStrips) {
                ledStrip.showStatus();
            }
        } else {
            LOG.error("No LED Strips configured.");
        }
    }


    /**
     * Handle jobs.
     */
    @Scheduled(fixedRate = 60000) // Run this once per minute
    public void handleJobs() {

        clearLEDStripStatuses();

        for (String server : jenkinsConfig.getServers()) { // iterate over servers

            JenkinsProperties jobs = retrieveJobs(server);

            if (jobs != null) {
                for (JenkinsJob job : jobs.getJobs()) {
                    if (jenkinsConfig.contains(server, job.getName())) {
                        updateStatus(jenkinsConfig.get(server, job.getName()), job.getName(), job.getColor());
                    }
                }
            }
        }

        showStatuses();
    }
}
