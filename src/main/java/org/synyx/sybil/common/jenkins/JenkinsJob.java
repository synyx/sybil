package org.synyx.sybil.common.jenkins;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * Jenkins Job.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class JenkinsJob {

    String name;
    String color;

    public JenkinsJob() {
    }


    public JenkinsJob(String name, String color) {

        this.name = name;
        this.color = color;
    }

    public String getName() {

        return name;
    }


    public void setName(String name) {

        this.name = name;
    }


    public String getColor() {

        return color;
    }


    public void setColor(String color) {

        this.color = color;
    }
}
