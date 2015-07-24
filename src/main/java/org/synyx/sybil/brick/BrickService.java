package org.synyx.sybil.brick;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.BrickMaster;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.synyx.sybil.LoadFailedException;
import org.synyx.sybil.brick.domain.BrickDTO;
import org.synyx.sybil.brick.domain.BrickDomain;

import java.io.IOException;

import java.util.List;

import javax.annotation.PostConstruct;


/**
 * BrickService.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Service
public class BrickService {

    private static final Logger LOG = LoggerFactory.getLogger(BrickService.class);

    BrickDTOService brickDTOService;

    @Autowired
    public BrickService(BrickDTOService brickDTOService) {

        this.brickDTOService = brickDTOService;
    }

    public IPConnection connect(BrickDTO brickDTO) {

        BrickDomain brickDomain = brickDTO.getDomain();
        IPConnection ipConnection = new IPConnection();

        try {
            ipConnection.connect(brickDomain.getHostname(), brickDomain.getPort());
        } catch (IOException | AlreadyConnectedException exception) {
            throw new BrickConnectionException("Error connecting to brick:", exception);
        }

        return ipConnection;
    }


    @PostConstruct
    public void resetAllBricks() {

        try {
            List<BrickDTO> brickDTOs = brickDTOService.getAllDTOs();

            for (BrickDTO brickDTO : brickDTOs) {
                reset(brickDTO);
            }
        } catch (BrickConnectionException | LoadFailedException exception) {
            handleError("Failed to reset bricks:", exception);
        }
    }


    private void handleError(String message, Exception exception) {

        LOG.error(message, exception);
    }


    private void reset(BrickDTO brickDTO) {

        BrickDomain brickDomain = brickDTO.getDomain();

        IPConnection ipConnection = connect(brickDTO);

        BrickMaster brickMaster = new BrickMaster(brickDomain.getUid(), ipConnection);

        try {
            brickMaster.reset();
            ipConnection.disconnect();
        } catch (NotConnectedException | TimeoutException exception) {
            throw new BrickConnectionException("Error resetting brick:", exception);
        }
    }
}
