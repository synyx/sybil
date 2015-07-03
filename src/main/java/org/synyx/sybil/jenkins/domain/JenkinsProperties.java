package org.synyx.sybil.jenkins.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;


/**
 * JenkinsProperties.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class JenkinsProperties {

    private JenkinsJob[] jobs;

    public JenkinsProperties() {

        // Default constructor deliberately left empty
    }


    public JenkinsProperties(JenkinsJob[] jobs) {

        this.jobs = Arrays.copyOf(jobs, jobs.length);
    }

    public JenkinsJob[] getJobs() {

        return Arrays.copyOf(jobs, jobs.length);
    }


    public void setJobs(JenkinsJob[] jobs) {

        this.jobs = Arrays.copyOf(jobs, jobs.length);
    }
}
