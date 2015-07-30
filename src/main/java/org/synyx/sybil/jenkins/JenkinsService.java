package org.synyx.sybil.jenkins;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Profile;

import org.springframework.core.env.Environment;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.stereotype.Service;

import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import org.synyx.sybil.LoadFailedException;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStripConnectionException;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStripNotFoundException;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStripService;
import org.synyx.sybil.jenkins.domain.JenkinsJob;
import org.synyx.sybil.jenkins.domain.JenkinsProperties;
import org.synyx.sybil.jenkins.domain.JobConfig;
import org.synyx.sybil.jenkins.domain.ServerConfig;
import org.synyx.sybil.jenkins.domain.Status;
import org.synyx.sybil.jenkins.domain.StatusInformation;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PreDestroy;


/**
 * JenkinsService.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Service
public class JenkinsService {

    private static final Logger LOG = LoggerFactory.getLogger(JenkinsService.class);
    private static final long SCHEDULED_TIME_IN_MS = 60000;
    private static final int DELAY_DIVISOR = 4;

    private final ObjectMapper objectMapper;
    private final LEDStripService ledStripService;
    private final String configDirectory;
    private final String jenkinsServerConfigFile;
    private final RestTemplate restTemplate;

    @Autowired
    public JenkinsService(ObjectMapper objectMapper, LEDStripService ledStripService, Environment environment,
        RestTemplate restTemplate) {

        this.objectMapper = objectMapper;
        this.ledStripService = ledStripService;
        this.restTemplate = restTemplate;
        configDirectory = environment.getProperty("path.to.configfiles");
        jenkinsServerConfigFile = environment.getProperty("jenkins.configfile");
    }

    @PreDestroy
    public void turnOffAllLEDStrips() {

        try {
            ledStripService.turnOffAllLEDStrips();
        } catch (LoadFailedException | LEDStripConnectionException exception) {
            handleError("Error turning off LED strips:", exception);
        }
    }


    private void handleError(String message, Exception exception) {

        LOG.error(message, exception);
    }


    @Profile("default")
    @Scheduled(initialDelay = SCHEDULED_TIME_IN_MS / DELAY_DIVISOR, fixedRate = SCHEDULED_TIME_IN_MS)
    public void runScheduled() {

        Map<String, HttpEntity<JenkinsProperties[]>> authorizations;
        Map<String, List<JobConfig>> jobConfigs;

        try {
            authorizations = loadAuthorizations();
            jobConfigs = loadJobConfigs();
        } catch (LoadFailedException exception) {
            handleError("Error loading Jenkins configuration:", exception);

            return;
        }

        List<String> servers = new ArrayList<>(authorizations.keySet());
        List<JenkinsJob> jobs;
        Map<String, StatusInformation> ledStripStatuses = new HashMap<>();

        for (String server : servers) {
            try {
                jobs = getJobsFromJenkins(server, authorizations.get(server));
                ledStripStatuses = getLEDStripStatusesFromJobs(jobs, jobConfigs.get(server), ledStripStatuses);
            } catch (RestClientException exception) {
                handleError("Error retrieving jobs from Jenkins:", exception);
            }
        }

        applyStatuses(ledStripStatuses);
    }


    private Map<String, HttpEntity<JenkinsProperties[]>> loadAuthorizations() {

        Map<String, HttpEntity<JenkinsProperties[]>> authorizations = new HashMap<>();

        List<ServerConfig> serverConfigs;

        try {
            serverConfigs = objectMapper.readValue(new File(jenkinsServerConfigFile),
                    new TypeReference<List<ServerConfig>>() {
                    });
        } catch (IOException exception) {
            throw new LoadFailedException("Error loading jenkins server config:", exception);
        }

        for (ServerConfig serverConfig : serverConfigs) {
            authorizations.put(serverConfig.getUrl(), serverConfig.getHeader());
        }

        return authorizations;
    }


    private Map<String, List<JobConfig>> loadJobConfigs() {

        try {
            return objectMapper.readValue(new File(configDirectory + "jenkins.json"),
                    new TypeReference<Map<String, List<JobConfig>>>() {
                    });
        } catch (IOException exception) {
            throw new LoadFailedException("Error loading jenkins config:", exception);
        }
    }


    private List<JenkinsJob> getJobsFromJenkins(String server, HttpEntity<JenkinsProperties[]> authorization) {

        ResponseEntity<JenkinsProperties> response = restTemplate.exchange(server + "/api/json", HttpMethod.GET,
                authorization, JenkinsProperties.class);

        return Arrays.asList(response.getBody().getJobs());
    }


    private Map<String, StatusInformation> getLEDStripStatusesFromJobs(List<JenkinsJob> jobs,
        List<JobConfig> jobConfigs, Map<String, StatusInformation> ledStripStatuses) {

        if (jobConfigs == null) {
            return ledStripStatuses;
        }

        for (JenkinsJob job : jobs) {
            StatusInformation jobStatus = getStatusFromJob(job);
            List<String> ledStrips = getLedStripFromConfiguredJob(job, jobConfigs);

            if (ledStrips.isEmpty()) {
                continue;
            }

            for (String ledStrip : ledStrips) {
                ledStripStatuses.put(ledStrip, higherStatus(jobStatus, ledStripStatuses.get(ledStrip)));
            }
        }

        return ledStripStatuses;
    }


    private StatusInformation getStatusFromJob(JenkinsJob job) {

        StatusInformation statusInformation;

        switch (job.getColor()) {
            case "red":
            case "red_anime":
                statusInformation = new StatusInformation(job.getName(), Status.CRITICAL);
                break;

            case "yellow":
            case "yellow_anime":
                statusInformation = new StatusInformation(job.getName(), Status.WARNING);
                break;

            default:
                statusInformation = new StatusInformation(job.getName(), Status.OKAY);
                break;
        }

        return statusInformation;
    }


    private List<String> getLedStripFromConfiguredJob(JenkinsJob job, List<JobConfig> jobConfigs) {

        List<String> result = new ArrayList<>();

        for (JobConfig jobConfig : jobConfigs) {
            if (jobConfig.getName().equals(job.getName())) {
                result.add(jobConfig.getLedstrip());
            }
        }

        return result;
    }


    private StatusInformation higherStatus(StatusInformation newStatus, StatusInformation currentStatus) {

        if (currentStatus == null || newStatus.getStatus().ordinal() > currentStatus.getStatus().ordinal()) {
            return newStatus;
        } else {
            return currentStatus;
        }
    }


    private void applyStatuses(Map<String, StatusInformation> ledStripStatuses) {

        for (String ledStrip : ledStripStatuses.keySet()) {
            try {
                ledStripService.handleStatus(ledStrip, ledStripStatuses.get(ledStrip));
            } catch (LoadFailedException | LEDStripConnectionException | LEDStripNotFoundException exception) {
                handleError("Error setting status on LED strip:", exception);
            }
        }
    }
}
