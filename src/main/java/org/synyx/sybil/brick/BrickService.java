package org.synyx.sybil.brick;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.BrickMaster;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import org.springframework.stereotype.Service;

import org.synyx.sybil.brick.domain.BrickDTO;
import org.synyx.sybil.brick.domain.BrickDomain;

import java.io.IOException;


/**
 * BrickService.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Service
public class BrickService {

    IPConnection connect(BrickDTO brickDTO) {

        BrickDomain brickDomain = brickDTO.getDomain();
        IPConnection ipConnection = new IPConnection();

        try {
            ipConnection.connect(brickDomain.getHostname(), brickDomain.getPort());
        } catch (IOException | AlreadyConnectedException exception) {
            throw new BrickConnectionException("Error connecting to brick:", exception);
        }

        return ipConnection;
    }


    void reset(BrickDTO brickDTO) {

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
