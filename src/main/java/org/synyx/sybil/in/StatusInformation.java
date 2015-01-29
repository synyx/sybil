package org.synyx.sybil.in;

import java.time.OffsetDateTime;
import java.time.ZoneId;


/**
 * Represents a certain status at a certain time. Immutable.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class StatusInformation {

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

        System.out.println(date.toString());
    }


    /**
     * Creates a new StatusInformation object with a default priority of 100.
     *
     * @param  source  Source of the status information.
     * @param  status  Status.
     */
    public StatusInformation(String source, Status status) {

        this(source, status, 100);
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
