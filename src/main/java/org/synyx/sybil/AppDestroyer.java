package org.synyx.sybil;

import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import org.synyx.sybil.bricklet.output.ledstrip.LEDStripDTOService;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStripService;
import org.synyx.sybil.bricklet.output.ledstrip.domain.LEDStripDTO;

import java.io.IOException;

import java.util.List;


/**
 * AppDestroyer.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Component
public class AppDestroyer {

    private LEDStripDTOService ledStripDTOService;
    private LEDStripService ledStripService;

    @Autowired
    public AppDestroyer(LEDStripDTOService dtoService, LEDStripService stripService) {

        ledStripDTOService = dtoService;
        ledStripService = stripService;
    }

    public void turnOffAllLEDStrips() {

        final Logger LOG = LoggerFactory.getLogger(AppDestroyer.class);

        try {
            List<LEDStripDTO> ledstrips = ledStripDTOService.getAllDTOs();

            for (LEDStripDTO ledStripDTO : ledstrips) {
                ledStripService.turnOff(ledStripDTO);
            }
        } catch (IOException | TimeoutException | NotConnectedException exception) {
            LOG.error("Error turning off LED strips: {}", exception);
        }
    }
}
