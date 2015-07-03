package org.synyx.sybil.jenkins.domain;

import java.time.OffsetDateTime;
import java.time.ZoneId;


/**
 * Represents a certain status at a certain time. Immutable.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class StatusInformation {

    private static final int DEFAULT_PRIORITY = 100;

    private final Status status;
    private final String source;
    private final OffsetDateTime date;
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
        this.date = OffsetDateTime.now(ZoneId.of("UTC"));
    }


    /**
     * Creates a new StatusInformation object with a default priority of 100.
     *
     * @param  source  Source of the status information.
     * @param  status  Status.
     */
    public StatusInformation(String source, Status status) {

        this(source, status, DEFAULT_PRIORITY);
    }

    public Status getStatus() {

        return status;
    }


    public String getSource() {

        return source;
    }


    public OffsetDateTime getDate() {

        return date;
    }


    public int getPriority() {

        return priority;
    }
}
