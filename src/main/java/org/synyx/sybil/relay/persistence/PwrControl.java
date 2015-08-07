package org.synyx.sybil.relay.persistence;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import org.apache.commons.codec.binary.Base64;

import org.springframework.http.HttpHeaders;

import java.nio.charset.Charset;


/**
 * NetControl.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class PwrControl {

    private String host;

    private String user;

    private String password;

    public PwrControl(String host, String user, String password) {

        this.host = host;
        this.user = user;
        this.password = password;
    }


    public PwrControl() {

        // default constructor deliberately left empty
    }

    public String getHost() {

        return host;
    }


    public HttpHeaders getHeader() {

        HttpHeaders headers = new HttpHeaders();

        headers.set("Authorization",
            "Basic "
            + new String(Base64.encodeBase64((user + ":" + password).getBytes(Charset.forName("US-ASCII")))));

        return headers;
    }
}
