package org.synyx.sybil.out;

import org.junit.Test;

import org.synyx.sybil.in.Status;
import org.synyx.sybil.in.StatusInformation;


public class SingleLEDOutputTest {

    @Test
    public void testShowStatus() throws Exception {

        SingleStatusOutput out = new SingleLEDOutput("localhost", 4223, "p3c", 2812, 10, 30);
        System.out.println("Setting to WARNING");
        out.showStatus(new StatusInformation("Test 1", Status.WARNING));
        System.out.println("Sleeping now");
        Thread.sleep(5000);
        System.out.println("Setting to CRITICAL");
        out.showStatus(new StatusInformation("Test 1", Status.CRITICAL));
        System.out.println("Sleeping now");
        Thread.sleep(5000);
        System.out.println("Setting to OKAY");
        out.showStatus(new StatusInformation("Test 1", Status.OKAY));
        out.close();
    }
}
