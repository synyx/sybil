package org.synyx.sybil.jenkins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Profile;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.stereotype.Service;

import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import org.synyx.sybil.bricklet.output.ledstrip.Color;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStrip;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStripService;
import org.synyx.sybil.bricklet.output.ledstrip.SingleStatusOnLEDStrip;
import org.synyx.sybil.bricklet.output.ledstrip.database.LEDStripDomain;
import org.synyx.sybil.jenkins.config.JenkinsConfig;
import org.synyx.sybil.jenkins.domain.JenkinsJob;
import org.synyx.sybil.jenkins.domain.JenkinsProperties;
import org.synyx.sybil.jenkins.domain.Status;
import org.synyx.sybil.jenkins.domain.StatusInformation;

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

    private static final int ONE_MINUTE_IN_MILLISECONDS = 60000;
    private static final int TEN_MINUTES_IN_MILLISECONDS = 600000;

    private static final Logger LOG = LoggerFactory.getLogger(JenkinsService.class);
    private final RestTemplate restTemplate = new RestTemplate();
    private final JenkinsConfig jenkinsConfig;
    private final LEDStripService ledStripService;

    /**
     * Instantiates a new Jenkins service.
     *
     * @param  jenkinsConfig  The Jenkins config bean, autowired in.
     * @param  ledStripService  the output lED strip registry
     */
    @Autowired
    public JenkinsService(JenkinsConfig jenkinsConfig, LEDStripService ledStripService) {

        this.jenkinsConfig = jenkinsConfig;
        this.ledStripService = ledStripService;
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
        } catch (RestClientException exception) {
            LOG.warn("{}: {}", serverURL, exception.toString());

            return null;
        }
    }


    /**
     * Update the status of the passed LED Strips, IF the new status is HIGHER than the old one.
     *
     * @param  ledStrips  The LED SingleStatusOnLEDStrip objects on which to update the status.
     * @param  jobName  The name of the job the status came from.
     * @param  jobStatus  The job's status.
     */
    private void updateStatus(List<SingleStatusOnLEDStrip> ledStrips, String jobName, String jobStatus) {

        StatusInformation statusInformation = getStatusInformationFromJobStatus(jobName, jobStatus);

        for (SingleStatusOnLEDStrip ledStrip : ledStrips) {
            if (isNewStatusHigherThanCurrent(statusInformation.getStatus(), ledStrip.getStatus())) {
                ledStrip.setStatus(statusInformation);
            }
        }
    }


    private StatusInformation getStatusInformationFromJobStatus(String jobName, String jobStatus) {

        StatusInformation statusInformation;

        switch (jobStatus) {
            case "red":
            case "red_anime":
                statusInformation = new StatusInformation(jobName, Status.CRITICAL);
                break;

            case "yellow":
            case "yellow_anime":
                statusInformation = new StatusInformation(jobName, Status.WARNING);
                break;

            default:
                statusInformation = new StatusInformation(jobName, Status.OKAY);
                break;
        }

        return statusInformation;
    }


    private boolean isNewStatusHigherThanCurrent(Status newStatus, Status currentStatus) {

        return newStatus.ordinal() > currentStatus.ordinal();
    }


    /**
     * Destroy void. Is called when the program ends. Turns off all the LED Strips.
     */
    @PreDestroy
    public void destroy() {

        List<LEDStripDomain> ledStripDomains = ledStripService.getAllDomains();

        for (LEDStripDomain ledStripDomain : ledStripDomains) {
            LEDStrip ledStrip = ledStripService.getLEDStrip(ledStripDomain);
            ledStrip.setFill(Color.BLACK);
            ledStrip.updateDisplay();
        }
    }


    /**
     * Clear all statuses on SingleStatusOnLEDStrips, so priority sorting can commence.
     */
    private void clearLEDStripStatuses() {

        Set<SingleStatusOnLEDStrip> allLEDStrips = jenkinsConfig.getAll();

        if (allLEDStrips == null) {
            LOG.warn("No LED Strips configured.");
        } else {
            for (SingleStatusOnLEDStrip ledStrip : allLEDStrips) {
                ledStrip.setStatus(new StatusInformation("Clear", Status.OKAY));
            }
        }
    }


    /**
     * Show the statuses that were set on the LED Strips.
     */
    private void showStatuses() {

        Set<SingleStatusOnLEDStrip> allLEDStrips = jenkinsConfig.getAll();

        if (allLEDStrips == null) {
            LOG.warn("No LED Strips configured.");
        } else {
            for (SingleStatusOnLEDStrip ledStrip : allLEDStrips) {
                ledStrip.showStatus();
            }
        }
    }


    /**
     * Clear the current statuses, iterate over servers and jobs, set their statuses and show them.
     */
    public void handleJobs() {

        clearLEDStripStatuses();

        for (String server : jenkinsConfig.getServers()) {
            JenkinsProperties jobs = retrieveJobs(server);

            if (jobs != null) {
                for (JenkinsJob job : jobs.getJobs()) {
                    if (jenkinsConfig.contains(server, job.getName())) {
                        updateStatus(jenkinsConfig.getSingleStatusOnLEDStrip(server, job.getName()), job.getName(),
                            job.getColor());
                    }
                }
            }
        }

        showStatuses();
    }


    /**
     * Run every minute.
     */
    @Profile("default")
    @Scheduled(fixedRate = ONE_MINUTE_IN_MILLISECONDS)
    public void runEveryMinute() {

        handleJobs();
    }


    /**
     * Run every ten minutes. Just here to keep the garbage collector from eating this in the dev profile.
     */
    @Profile("dev")
    @Scheduled(fixedRate = TEN_MINUTES_IN_MILLISECONDS)
    public void runEveryTenMinutes() {

        LOG.debug("Runs every 10 minutes!");
    }
}
