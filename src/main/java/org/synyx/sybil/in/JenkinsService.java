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
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * The Jenkins config object, contains all the configured servers and jobs.
     */
    private final JenkinsConfig jenkinsConfig;

    /**
     * Instantiates a new Jenkins service.
     *
     * @param  jenkinsConfig  The Jenkins config bean, autowired in.
     */
    @Autowired
    public JenkinsService(JenkinsConfig jenkinsConfig) {

        this.jenkinsConfig = jenkinsConfig;
    }

    /**
     * Retrieve the list of jobs from the Jenkins API.
     *
     * @param  serverURL  The Jenkins server URL (e.g. "http://jenkins.company.name")
     *
     * @return  A JenkinsProperties object, deserialized from the API.
     */
    private JenkinsProperties retrieveJobs(String serverURL) {

        HttpEntity<JenkinsProperties[]> authorizationHeaderEntity = jenkinsConfig.getServer(serverURL);

        try {
            ResponseEntity<JenkinsProperties> response = restTemplate.exchange(serverURL + "/api/json", HttpMethod.GET,
                    authorizationHeaderEntity, JenkinsProperties.class);

            return response.getBody();
        } catch (Exception e) {
            LOG.error(serverURL + ": " + e.getMessage());

            return null;
        }
    }


    /**
     * Update the status of the passed LED Strips, IF the new status is HIGHER than the old one.
     *
     * @param  ledStrips  The LED SingleStatusOnLEDStrip objects on which to update the status.
     * @param  jobName  The name of the job the status came from.
     * @param  status  The job's status.
     */
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
                if (isNewStatusHigherThanCurrent(statusInformation.getStatus(), ledStrip.getStatus()))
                    ledStrip.setStatus(statusInformation);
            }
        }
    }


    private boolean isNewStatusHigherThanCurrent(Status newStatus, Status currentStatus) {

        return newStatus.ordinal() > currentStatus.ordinal();
    }


    /**
     * Destroy void. Is called when the program ends. Turns off all the LED Strips.
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


    /**
     * Show the statuses that were set on the LED Strips.
     */
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
     * Clear the current statuses, iterate over servers and jobs, set their statuses and show them.
     */
    @Scheduled(fixedRate = 60000)
    public void handleJobs() {

        clearLEDStripStatuses();

        for (String server : jenkinsConfig.getServers()) {
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
