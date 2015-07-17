package org.synyx.sybil.jenkins.domain;

/**
 * JenkinsConfiguration.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class ConfiguredJob {

    private String name;
    private String ledstrip;

    public ConfiguredJob(String name, String ledstrip) {

        this.name = name;
        this.ledstrip = ledstrip;
    }


    public ConfiguredJob() {

        // Default constructor deliberately left empty
    }

    public String getName() {

        return name;
    }


    public String getLedstrip() {

        return ledstrip;
    }
}
