package org.synyx.sybil.jenkins.persistence;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import org.apache.commons.codec.binary.Base64;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import org.synyx.sybil.jenkins.JenkinsProperties;

import java.nio.charset.Charset;


/**
 * ConfiguredServer.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class ServerConfig {

    private String url;
    private String user;
    private String key;

    public ServerConfig(String url, String user, String key) {

        this.url = url;
        this.user = user;
        this.key = key;
    }


    public ServerConfig() {

        // Default constructor deliberately left empty
    }

    public String getUrl() {

        return url;
    }


    public HttpEntity<JenkinsProperties[]> getHeader() {

        HttpHeaders headers = new HttpHeaders();

        headers.set("Authorization",
            "Basic "
            + new String(Base64.encodeBase64((user + ":" + key).getBytes(Charset.forName("US-ASCII")))));

        return new HttpEntity<>(headers);
    }
}
