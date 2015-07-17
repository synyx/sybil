package org.synyx.sybil;

import org.springframework.http.converter.HttpMessageNotWritableException;


/**
 * ConfigLoaderException.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class LoadFailedException extends HttpMessageNotWritableException {

    public LoadFailedException(String message) {

        super(message);
    }


    public LoadFailedException(String msg, Throwable cause) {

        super(msg, cause);
    }
}
