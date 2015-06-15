package org.synyx.sybil.brick;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.tinkerforge.BrickMaster;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

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

    private BrickService brickService;

    private String configDir;

    private ObjectMapper mapper;

    // length of reset timeout in seconds
    private int timeoutLength;

    @Autowired
    public BrickConfigLoader(BrickService brickService, ObjectMapper mapper, Environment environment) {

        this.brickService = brickService;
        this.mapper = mapper;
        this.configDir = environment.getProperty("path.to.configfiles");
        this.timeoutLength = Integer.parseInt(environment.getProperty("brick.reset.timeout.seconds"));
    }

    /**
     * Load bricks config.
     */
    public void loadBricksConfig() {

        if (HealthController.getHealth() == Status.OKAY) {
            LOG.info("Loading Brick configuration");

            try {
                List<BrickDomain> bricks = mapper.readValue(new File(configDir + "bricks.json"),
                        new TypeReference<List<BrickDomain>>() {
                        });

                brickService.deleteAllBrickDomains();

                brickService.saveBrickDomains(bricks); // ... simply dump them into the database
            } catch (IOException e) {
                LOG.error("Error loading bricks.json: {}", e.toString());
                HealthController.setHealth(Status.CRITICAL, "loadBricksConfig");
            }
        }
    }


    /**
     * Reset all bricks.
     */
    public void resetBricks() {

        if (HealthController.getHealth() == Status.OKAY) {
            LOG.info("Resetting bricks");

            try {
                List<BrickDomain> bricks = brickService.getAllBrickDomains();

                for (BrickDomain brick : bricks) {
                    IPConnection ipConnection = brickService.getIPConnection(brick);

                    BrickMaster brickMaster = brickService.getBrickMaster(brick.getUid(), ipConnection);
                    brickMaster.reset();

                    Short connectionEstablished = null;
                    long timeoutTime = Instant.now().getEpochSecond() + timeoutLength;

                    while (connectionEstablished == null) {
                        try {
                            connectionEstablished = brickMaster.getChipTemperature();
                        } catch (TimeoutException e) {
                            if (Instant.now().getEpochSecond() > timeoutTime) {
                                LOG.error("Error resetting brick {}: Timeout of {} second(s) was reached.",
                                    brick.getName(), timeoutLength);
                                HealthController.setHealth(Status.CRITICAL, "resetBricks");

                                break;
                            }
                        }
                    }
                }

                brickService.disconnectAll();
            } catch (TimeoutException | NotConnectedException e) {
                LOG.error("Error resetting bricks: {}", e.toString());
                HealthController.setHealth(Status.CRITICAL, "resetBricks");
            }
        }
    }
}
