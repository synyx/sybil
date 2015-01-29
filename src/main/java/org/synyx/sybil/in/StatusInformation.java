package org.synyx.sybil.in;

import java.time.LocalDateTime;


/**
 * Represents a certain status at a certain time. Immutable.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class StatusInformation {

    private final Status status;
    private final String source;
    private final LocalDateTime date;
    private final int priority;

    /**
     * Creates a new StatusInformation object.
     *
     * @param  source  Source of the status information.
     * @param  status  Status.
     * @param  priority  Normal priority is 100.
     */
    public StatusInformation(String source, Status status, int priority) {

        this.status = status;
        this.source = source;
        this.priority = priority;
        this.date = LocalDateTime.now(); // TODO: UTC?
    }


    /**
     * Creates a new StatusInformation object with a default priority of 100.
     *
     * @param  source  Source of the status information.
     * @param  status  Status.
     */
    public StatusInformation(String source, Status status) {

        this.status = status;
        this.source = source;
        this.priority = 100;
        this.date = LocalDateTime.now();
    }

    public Status getStatus() {

        return status;
    }


    public String getSource() {

        return source;
    }


    public LocalDateTime getDate() {

        return date;
    }


    public int getPriority() {

        return priority;
    }
}
