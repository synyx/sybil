package org.synyx.sybil.jenkins.domain;

/**
 * JenkinsConfiguration.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class JobConfig {

    private String name;
    private String ledstrip;

    public JobConfig(String name, String ledstrip) {

        this.name = name;
        this.ledstrip = ledstrip;
    }


    public JobConfig() {

        // Default constructor deliberately left empty
    }

    public String getName() {

        return name;
    }


    public String getLedstrip() {

        return ledstrip;
    }
}
