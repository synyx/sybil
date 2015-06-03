package org.synyx.sybil.brick;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.tinkerforge.BrickMaster;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

import org.neo4j.helpers.collection.IteratorUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.env.Environment;

import org.springframework.stereotype.Component;

import org.synyx.sybil.api.HealthController;
import org.synyx.sybil.brick.database.BrickDomain;
import org.synyx.sybil.brick.database.BrickRepository;
import org.synyx.sybil.jenkins.domain.Status;

import java.io.File;
import java.io.IOException;

import java.time.Instant;

import java.util.ArrayList;
import java.util.List;


/**
 * BrickConfigLoader.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Component
public class BrickConfigLoader {

    private static final Logger LOG = LoggerFactory.getLogger(BrickConfigLoader.class);

    private BrickRepository brickRepository;

    private BrickRegistry brickRegistry;

    private String configDir;

    private ObjectMapper mapper;

    private GraphDatabaseService graphDatabaseService;

    // length of reset timeout in seconds
    int timeoutLength;

    @Autowired
    public BrickConfigLoader(BrickRepository brickRepository, BrickRegistry brickRegistry, ObjectMapper mapper,
        GraphDatabaseService graphDatabaseService, Environment environment) {

        this.brickRepository = brickRepository;
        this.brickRegistry = brickRegistry;
        this.mapper = mapper;
        this.graphDatabaseService = graphDatabaseService;
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

                brickRepository.deleteAll();

                brickRepository.save(bricks); // ... simply dump them into the database
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
                List<BrickDomain> bricks;

                try(Transaction tx = graphDatabaseService.beginTx()) { // begin transaction

                    // get all Bricks from database and cast them into a list so that they're actually fetched
                    bricks = new ArrayList<>(IteratorUtil.asCollection(brickRepository.findAll()));

                    // end transaction
                    tx.success();
                }

                for (BrickDomain brick : bricks) {
                    IPConnection ipConnection = brickRegistry.get(brick);

                    BrickMaster brickMaster = new BrickMaster(brick.getUid(), ipConnection);
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

                brickRegistry.disconnectAll();
            } catch (TimeoutException | NotConnectedException e) {
                LOG.error("Error resetting bricks: {}", e.toString());
                HealthController.setHealth(Status.CRITICAL, "resetBricks");
            }
        }
    }
}
