package org.synyx.sybil.brick.service;

/**
 * BrickNotFoundException.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class BrickNotFoundException extends RuntimeException {

    public BrickNotFoundException(String message) {

        super(message);
    }


    public BrickNotFoundException(String message, Throwable cause) {

        super(message, cause);
    }
}
