package org.synyx.sybil.brick;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.BrickMaster;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import org.springframework.stereotype.Service;

import org.synyx.sybil.brick.domain.BrickConfig;
import org.synyx.sybil.brick.domain.BrickDTO;

import java.io.IOException;


/**
 * BrickService.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Service
public class BrickService {

    IPConnection connect(BrickDTO brickDTO) {

        BrickConfig brickConfig = brickDTO.getConfig();
        IPConnection ipConnection = new IPConnection();

        try {
            ipConnection.connect(brickConfig.getHostname(), brickConfig.getPort());
        } catch (IOException | AlreadyConnectedException exception) {
            throw new BrickConnectionException("Error connecting to brick:", exception);
        }

        return ipConnection;
    }


    void reset(BrickDTO brickDTO) {

        BrickConfig brickConfig = brickDTO.getConfig();

        IPConnection ipConnection = connect(brickDTO);

        BrickMaster brickMaster = new BrickMaster(brickConfig.getUid(), ipConnection);

        try {
            brickMaster.reset();
            ipConnection.disconnect();
        } catch (NotConnectedException | TimeoutException exception) {
            throw new BrickConnectionException("Error resetting brick:", exception);
        }
    }
}
