package org.synyx.sybil.brick.service;

/**
 * BrickConnectionException.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class BrickConnectionException extends RuntimeException {

    public BrickConnectionException(String message) {

        super(message);
    }


    public BrickConnectionException(String message, Throwable cause) {

        super(message, cause);
    }
}
