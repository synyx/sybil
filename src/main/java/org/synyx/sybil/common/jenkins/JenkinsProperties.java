package org.synyx.sybil.common.jenkins;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * JenkinsProperties.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class JenkinsProperties {

    JenkinsJob[] jobs;

    public JenkinsProperties() {
    }


    public JenkinsProperties(JenkinsJob[] jobs) {

        this.jobs = jobs;
    }

    public JenkinsJob[] getJobs() {

        return jobs;
    }


    public void setJobs(JenkinsJob[] jobs) {

        this.jobs = jobs;
    }
}
