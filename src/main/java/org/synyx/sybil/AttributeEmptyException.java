package org.synyx.sybil;

import org.springframework.http.converter.HttpMessageNotWritableException;


/**
 * AttributeEmptyException.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class AttributeEmptyException extends HttpMessageNotWritableException {

    public AttributeEmptyException(String message) {

        super(message);
    }


    public AttributeEmptyException(String msg, Throwable cause) {

        super(msg, cause);
    }
}
