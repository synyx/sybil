package org.synyx.sybil.jenkins.domain;

/**
 * ConfiguredServer.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class ConfiguredServer {

    private String url;
    private String user;
    private String key;

    public ConfiguredServer(String url, String user, String key) {

        this.url = url;
        this.user = user;
        this.key = key;
    }


    public ConfiguredServer() {

        // Default constructor deliberately left empty
    }

    public String getUrl() {

        return url;
    }


    public String getUser() {

        return user;
    }


    public String getKey() {

        return key;
    }
}
