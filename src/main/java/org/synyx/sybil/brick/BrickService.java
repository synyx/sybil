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
import org.synyx.sybil.brick.domain.Brick;

import java.io.IOException;

import javax.annotation.PostConstruct;


/**
 * BrickService.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Service
public class BrickService {

    private static final Logger LOG = LoggerFactory.getLogger(BrickService.class);

    private final BrickRepository brickRepository;

    @Autowired
    public BrickService(BrickRepository brickRepository) {

        this.brickRepository = brickRepository;
    }

    @PostConstruct
    public void resetAllBricks() {

        try {
            for (Brick brick : brickRepository.getAll()) {
                reset(brick);
            }
        } catch (BrickConnectionException | LoadFailedException exception) {
            LOG.error("Failed to reset bricks:", exception);
        }
    }


    public IPConnection connect(String name) {

        Brick brick = brickRepository.get(name);

        IPConnection ipConnection = new IPConnection();

        try {
            ipConnection.connect(brick.getHostname(), brick.getPort());
        } catch (IOException | AlreadyConnectedException exception) {
            throw new BrickConnectionException("Error connecting to brick:", exception);
        }

        return ipConnection;
    }


    private void reset(Brick brick) {

        IPConnection ipConnection = connect(brick.getName());

        BrickMaster brickMaster = new BrickMaster(brick.getUid(), ipConnection);

        try {
            brickMaster.reset();
            ipConnection.disconnect();
        } catch (NotConnectedException | TimeoutException exception) {
            throw new BrickConnectionException("Error resetting brick:", exception);
        }
    }
}
