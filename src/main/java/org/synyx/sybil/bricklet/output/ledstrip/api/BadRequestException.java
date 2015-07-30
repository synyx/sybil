package org.synyx.sybil.bricklet.output.ledstrip.api;

/**
 * BadRequestException.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {

        super(message);
    }


    public BadRequestException(String message, Throwable cause) {

        super(message, cause);
    }
}
