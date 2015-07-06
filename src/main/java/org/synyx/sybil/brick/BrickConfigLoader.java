package org.synyx.sybil.brick;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.tinkerforge.BrickMaster;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;
import com.tinkerforge.TinkerforgeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.env.Environment;

import org.springframework.stereotype.Component;

import org.synyx.sybil.api.HealthController;
import org.synyx.sybil.brick.database.BrickDomain;
import org.synyx.sybil.jenkins.domain.Status;

import java.io.File;
import java.io.IOException;

import java.time.Instant;

import java.util.List;


/**
 * BrickConfigLoader.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Component
public class BrickConfigLoader {

    private static final Logger LOG = LoggerFactory.getLogger(BrickConfigLoader.class);
    private final OldBrickService brickService;
    private final String configDir;
    private final ObjectMapper mapper;
    private final int timeoutLengthInSeconds;

    @Autowired
    public BrickConfigLoader(OldBrickService brickService, ObjectMapper mapper, Environment environment) {

        this.brickService = brickService;
        this.mapper = mapper;
        this.configDir = environment.getProperty("path.to.configfiles");
        this.timeoutLengthInSeconds = Integer.parseInt(environment.getProperty("brick.reset.timeout.seconds"));
    }

    public void loadBricksConfig() {

        if (HealthController.getHealth() == Status.OKAY) {
            LOG.info("Loading Brick configuration");

            try {
                List<BrickDomain> bricks = mapper.readValue(new File(configDir + "bricks.json"),
                        new TypeReference<List<BrickDomain>>() {
                        });

                brickService.deleteAllDomains();

                brickService.saveDomains(bricks);
            } catch (IOException exception) {
                LOG.error("Error loading bricks.json: {}", exception);
                HealthController.setHealth(Status.CRITICAL, "loadBricksConfig");
            }
        }
    }


    public void resetAllBricks() {

        if (HealthController.getHealth() == Status.OKAY) {
            LOG.info("Resetting bricks");

            try {
                List<BrickDomain> bricks = brickService.getAllDomains();

                loopOverBricksAndResetEachOne(bricks);

                brickService.disconnectAll();
            } catch (TinkerforgeException exception) {
                LOG.error("Error resetting bricks: {}", exception);
                HealthController.setHealth(Status.CRITICAL, "resetBricks");
            }
        }
    }


    private void loopOverBricksAndResetEachOne(List<BrickDomain> bricks) throws TinkerforgeException {

        for (BrickDomain brick : bricks) {
            resetBrick(brick);
        }
    }


    public void resetBrick(BrickDomain brick) throws TinkerforgeException {

        IPConnection ipConnection = brickService.getIPConnection(brick);
        BrickMaster brickMaster = brickService.getBrickMaster(brick.getUid(), ipConnection);

        brickMaster.reset();

        waitForBrickReset(brick.getName(), brickMaster);
    }


    private void waitForBrickReset(String name, BrickMaster brickMaster) throws NotConnectedException {

        Short isConnectionEstablished = null;
        long timeoutTime = Instant.now().getEpochSecond() + timeoutLengthInSeconds;

        while (isConnectionEstablished == null) {
            try {
                isConnectionEstablished = brickMaster.getChipTemperature();
            } catch (TimeoutException e) { // NOSONAR Exception doesn't need to be logged, as it is simply for timing.

                if (isTimeoutReached(timeoutTime)) {
                    LOG.error("Error resetting brick {}: Timeout of {}s was reached.", name, timeoutLengthInSeconds);
                    HealthController.setHealth(Status.CRITICAL, "waitForBrickReset");

                    break;
                }
            }
        }
    }


    private boolean isTimeoutReached(long timeoutTime) {

        return Instant.now().getEpochSecond() > timeoutTime;
    }
}
