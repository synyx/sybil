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
import org.synyx.sybil.out.StatusesOnLEDStrip;

import java.nio.charset.Charset;

import javax.annotation.PreDestroy;


/**
 * JenkinsService. Reads the Jenkins API in regular intervals, compares it to the Jenkins build alert configuration.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Service
public class JenkinsService {

    RestTemplate rest = new RestTemplate();

    HttpHeaders headers = new HttpHeaders();

    HttpEntity<JenkinsProperties[]> request;

    JenkinsJob[] jobs;

    JenkinsConfig jenkinsConfig;

    String jenkinsURL;

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

        request = new HttpEntity<>(headers);

        this.jenkinsConfig = jenkinsConfig;
    }

    private JenkinsProperties retrieveJobs() {

        ResponseEntity<JenkinsProperties> response = rest.exchange(jenkinsURL, HttpMethod.GET, request,
                JenkinsProperties.class);

        return response.getBody();
    }


    private void showStatus(StatusesOnLEDStrip statusesOnLEDStrip, String jobName, String status) {

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
            statusesOnLEDStrip.showStatus(statusInformation);
        }
    }


    @PreDestroy
    public void destroy() {

        for (StatusesOnLEDStrip statusesOnLEDStrip : jenkinsConfig.getAll()) {
            statusesOnLEDStrip.turnOff();
        }
    }


    @Scheduled(fixedRate = 15000)
    public void handleJobs() {

        jobs = retrieveJobs().getJobs();

        for (JenkinsJob job : jobs) {
            if (jenkinsConfig.contains(job.getName())) {
                showStatus(jenkinsConfig.get(job.getName()), job.getName(), job.getColor());
            }
        }
    }
}
