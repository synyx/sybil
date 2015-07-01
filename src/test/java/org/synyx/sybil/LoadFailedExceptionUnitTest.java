package org.synyx.sybil;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.junit.runners.JUnit4;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.is;


/**
 * ConfigLoaderExceptionUnitTest.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@RunWith(JUnit4.class)
public class LoadFailedExceptionUnitTest {

    @Test
    public void throwLoadFailedExceptionWithMessage() {

        String expected = "This is a test.";
        String actual;

        try {
            throw new LoadFailedException(expected);
        } catch (LoadFailedException exception) {
            actual = exception.getMessage();
        }

        assertThat(actual, is(expected));
    }
}
