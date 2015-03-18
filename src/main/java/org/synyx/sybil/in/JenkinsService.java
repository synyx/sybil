package org.synyx.sybil.in;

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
     * The Jenkins config.
     */
    JenkinsConfig jenkinsConfig;

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

        ResponseEntity<JenkinsProperties> response = rest.exchange(server + "/api/json", HttpMethod.GET, requestEntity,
                JenkinsProperties.class);

        return response.getBody();
    }


    private void updateStatus(List<SingleStatusOnLEDStrip> ledStrips, String jobName, String status) {

        StatusInformation statusInformation = null;

        switch (status) {
            case "red":
            case "red_anime":
                statusInformation = new StatusInformation(jobName, Status.CRITICAL);
                break;

            case "yellow":
            case "yello_anime":
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

        for (String server : jenkinsConfig.getServers()) { // iterate over servers

            Collection<List<SingleStatusOnLEDStrip>> allLEDStrips = jenkinsConfig.getAll(server);

            for (List<SingleStatusOnLEDStrip> ledStripList : allLEDStrips) {
                for (SingleStatusOnLEDStrip ledStrip : ledStripList) {
                    ledStrip.turnOff();
                }
            }
        }
    }


    /**
     * Clear all statuses on SingleStatusOnLEDStrips, so priority sorting can commence.
     */
    private void clearLEDStripStatuses(String server) {

        Collection<List<SingleStatusOnLEDStrip>> allLEDStrips = jenkinsConfig.getAll(server);

        for (List<SingleStatusOnLEDStrip> ledStripList : allLEDStrips) {
            for (SingleStatusOnLEDStrip ledStrip : ledStripList) {
                ledStrip.setStatus(new StatusInformation("Clear", Status.OKAY));
            }
        }
    }


    private void showStatuses(String server) {

        Collection<List<SingleStatusOnLEDStrip>> allLEDStrips = jenkinsConfig.getAll(server);

        for (List<SingleStatusOnLEDStrip> ledStripList : allLEDStrips) {
            for (SingleStatusOnLEDStrip ledStrip : ledStripList) {
                ledStrip.showStatus();
            }
        }
    }


    /**
     * Handle jobs.
     */
    @Scheduled(fixedRate = 60000) // Run this once per minute
    public void handleJobs() {

        for (String server : jenkinsConfig.getServers()) { // iterate over servers

            JenkinsJob[] jobs = retrieveJobs(server).getJobs();

            clearLEDStripStatuses(server);

            for (JenkinsJob job : jobs) {
                if (jenkinsConfig.contains(server, job.getName())) {
                    updateStatus(jenkinsConfig.get(server, job.getName()), job.getName(), job.getColor());
                }
            }

            showStatuses(server);
        }
    }
}
