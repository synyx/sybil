package org.synyx.sybil.bricklet.output.relay;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.env.Environment;

import org.springframework.stereotype.Component;

import org.synyx.sybil.api.HealthController;
import org.synyx.sybil.brick.database.BrickDomain;
import org.synyx.sybil.brick.database.BrickRepository;
import org.synyx.sybil.bricklet.BrickletNameRegistry;
import org.synyx.sybil.bricklet.output.relay.database.RelayDomain;
import org.synyx.sybil.bricklet.output.relay.database.RelayRepository;
import org.synyx.sybil.jenkins.domain.Status;

import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.Map;


/**
 * RelayConfigLoader.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Component
public class RelayConfigLoader {

    // Logger
    private static final Logger LOG = LoggerFactory.getLogger(RelayConfigLoader.class);

    // Jackson ObjectMapper, maps JSON to Java Objects
    private ObjectMapper mapper = new ObjectMapper();

    // The place where the config files lie, taken from the injected environment (and thus ultimately a properties file)
    private String configDir;

    // The Repository to save Brick configuration data
    private BrickRepository brickRepository;

    // The Repository to save Relay configuration data
    private RelayRepository relayRepository;

    // Registers bricklets' names to make sure they are unique
    private BrickletNameRegistry brickletNameRegistry;

    @Autowired
    public RelayConfigLoader(ObjectMapper mapper, BrickRepository brickRepository, RelayRepository relayRepository,
        BrickletNameRegistry brickletNameRegistry, Environment environment) {

        this.mapper = mapper;
        this.brickRepository = brickRepository;
        this.relayRepository = relayRepository;
        this.brickletNameRegistry = brickletNameRegistry;
        configDir = environment.getProperty("path.to.configfiles");
    }

    public void loadRelayConfig() {

        LOG.info("Loading Relay configuration");

        if (HealthController.getHealth() == Status.OKAY) {
            try {
                List<Map<String, Object>> relays = mapper.readValue(new File(configDir + "relays.json"),
                        new TypeReference<List<Map<String, Object>>>() {
                        });

                relayRepository.deleteAll();

                for (Map relay : relays) { // ... deserialize the data manually

                    String name = relay.get("name").toString();

                    if (brickletNameRegistry.contains(name)) {
                        LOG.error("Failed to load config for Relay {}: Name is not unique.", name);
                        HealthController.setHealth(Status.WARNING, "loadRelayConfig");

                        break;
                    }

                    brickletNameRegistry.add(name);

                    String uid = relay.get("uid").toString();

                    BrickDomain brick = brickRepository.findByName(relay.get("brick").toString()); // fetch the corresponding bricks from the repo

                    if (brick != null) { // if there was corresponding brick found in the repo...
                        relayRepository.save(new RelayDomain(name, uid, brick)); // ... save the relay.
                    } else { // if not...
                        LOG.error("Brick {} does not exist.", relay.get("brick").toString()); // ... error!
                        HealthController.setHealth(Status.WARNING, "loadRelayConfig");
                    }
                }
            } catch (IOException e) {
                LOG.error("Error loading relays.json: {}", e.toString());
                HealthController.setHealth(Status.CRITICAL, "loadRelayConfig");
            }
        }
    }
}
