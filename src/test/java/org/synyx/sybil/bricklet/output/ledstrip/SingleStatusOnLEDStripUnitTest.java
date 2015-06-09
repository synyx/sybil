package org.synyx.sybil.bricklet.output.ledstrip;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.mockito.runners.MockitoJUnitRunner;

import org.synyx.sybil.jenkins.domain.Status;
import org.synyx.sybil.jenkins.domain.StatusInformation;


/**
 * SingleStatusOnLEDStripUnitTest.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@RunWith(MockitoJUnitRunner.class)
public class SingleStatusOnLEDStripUnitTest {

    @Mock
    private LEDStrip ledStrip;

    private SingleStatusOnLEDStrip singleStatusOnLEDStrip;

    @Test
    public void testStatus() {

        singleStatusOnLEDStrip = new SingleStatusOnLEDStrip(ledStrip);

        InOrder inOrder = Mockito.inOrder(ledStrip);

        singleStatusOnLEDStrip.showStatus();

        inOrder.verify(ledStrip).setFill(Color.OKAY);
        inOrder.verify(ledStrip).updateDisplay();

        singleStatusOnLEDStrip.setStatus(new StatusInformation("Test", Status.WARNING));
        singleStatusOnLEDStrip.showStatus();

        inOrder.verify(ledStrip).setFill(Color.WARNING);
        inOrder.verify(ledStrip).updateDisplay();

        singleStatusOnLEDStrip.setStatus(new StatusInformation("Test", Status.CRITICAL));
        singleStatusOnLEDStrip.showStatus();

        inOrder.verify(ledStrip).setFill(Color.CRITICAL);
        inOrder.verify(ledStrip).updateDisplay();

        singleStatusOnLEDStrip.setStatus(new StatusInformation("Test", Status.OKAY));
        singleStatusOnLEDStrip.showStatus();

        inOrder.verify(ledStrip).setFill(Color.OKAY);
        inOrder.verify(ledStrip).updateDisplay();
    }


    @Test
    public void testCustomColors() {

        Color okay = new Color(12, 13, 14);
        Color warning = new Color(66, 66, 66);
        Color critical = new Color(255, 128, 0);

        singleStatusOnLEDStrip = new SingleStatusOnLEDStrip(ledStrip, okay, warning, critical);

        InOrder inOrder = Mockito.inOrder(ledStrip);

        singleStatusOnLEDStrip.showStatus();

        inOrder.verify(ledStrip).setFill(okay);
        inOrder.verify(ledStrip).updateDisplay();

        singleStatusOnLEDStrip.setStatus(new StatusInformation("Test", Status.WARNING));
        singleStatusOnLEDStrip.showStatus();

        inOrder.verify(ledStrip).setFill(warning);
        inOrder.verify(ledStrip).updateDisplay();

        singleStatusOnLEDStrip.setStatus(new StatusInformation("Test", Status.CRITICAL));
        singleStatusOnLEDStrip.showStatus();

        inOrder.verify(ledStrip).setFill(critical);
        inOrder.verify(ledStrip).updateDisplay();

        singleStatusOnLEDStrip.setStatus(new StatusInformation("Test", Status.OKAY));
        singleStatusOnLEDStrip.showStatus();

        inOrder.verify(ledStrip).setFill(okay);
        inOrder.verify(ledStrip).updateDisplay();
    }
}
