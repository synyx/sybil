package org.synyx.sybil;

/**
 * APIError.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class APIError {

    private final String error;

    public APIError(String error) {

        this.error = error;
    }

    public String getError() {

        return error;
    }
}
