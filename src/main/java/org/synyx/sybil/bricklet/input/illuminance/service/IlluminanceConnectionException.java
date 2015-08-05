package org.synyx.sybil.bricklet.input.illuminance.service;

/**
 * IlluminanceConnectionException.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class IlluminanceConnectionException extends RuntimeException {

    public IlluminanceConnectionException(String message) {

        super(message);
    }


    public IlluminanceConnectionException(String message, Throwable cause) {

        super(message, cause);
    }
}
