package org.synyx.sybil;

/**
 * ConfigLoaderException.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class LoadFailedException extends RuntimeException {

    public LoadFailedException(String message) {

        super(message);
    }


    public LoadFailedException(String msg, Throwable cause) {

        super(msg, cause);
    }
}
