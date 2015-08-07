package org.synyx.sybil.relay.persistence;

/**
 * Relay.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class Relay {

    private String name;

    private String host;

    private int number;

    public Relay(String name, String host, int number) {

        this.name = name;
        this.host = host;
        this.number = number;
    }


    public Relay() {

        // default constructor deliberately left empty
    }

    public String getName() {

        return name;
    }


    public String getHost() {

        return host;
    }


    public int getNumber() {

        return number;
    }
}
