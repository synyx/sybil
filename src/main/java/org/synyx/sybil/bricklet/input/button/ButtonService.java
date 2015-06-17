package org.synyx.sybil.bricklet.input.button;

import com.tinkerforge.BrickletIO4;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

import org.neo4j.helpers.collection.IteratorUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.synyx.sybil.brick.BrickService;
import org.synyx.sybil.bricklet.BrickletService;
import org.synyx.sybil.bricklet.input.button.database.ButtonDomain;
import org.synyx.sybil.bricklet.input.button.database.ButtonRepository;
import org.synyx.sybil.bricklet.output.relay.RelayService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * LEDStripRegistry.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Service // Annotated so Spring finds and injects it.
public class ButtonService implements BrickletService {

    private static final Logger LOG = LoggerFactory.getLogger(ButtonService.class);

    private Map<ButtonDomain, BrickletIO4> buttons = new HashMap<>();
    private Map<String, ButtonDomain> domains = new HashMap<>();
    private BrickService brickService;
    private RelayService relayService;
    private ButtonRepository buttonRepository;
    private GraphDatabaseService graphDatabaseService;

    // Constructor, called when Spring autowires it somewhere. Dependencies are injected.

    /**
     * Instantiates a new Button sensor registry.
     *
     * @param  brickService  the brick registry
     * @param  relayService  the output relay registry
     * @param  buttonRepository  the button repository
     * @param  graphDatabaseService  the graph database service
     */
    @Autowired
    public ButtonService(BrickService brickService, RelayService relayService, ButtonRepository buttonRepository,
        GraphDatabaseService graphDatabaseService) {

        this.brickService = brickService;
        this.relayService = relayService;
        this.buttonRepository = buttonRepository;
        this.graphDatabaseService = graphDatabaseService;
    }

    /**
     * Save domain.
     *
     * @param  buttonDomain  the button domain
     *
     * @return  the button domain
     */
    public ButtonDomain saveDomain(ButtonDomain buttonDomain) {

        return buttonRepository.save(buttonDomain);
    }


    /**
     * Gets domain.
     *
     * @param  name  the name
     *
     * @return  the domain
     */
    public ButtonDomain getDomain(String name) {

        return buttonRepository.findByName(name);
    }


    /**
     * Gets all domains.
     *
     * @return  the all domains
     */
    public List<ButtonDomain> getAllDomains() {

        List<ButtonDomain> buttonDomains;

        try(Transaction tx = graphDatabaseService.beginTx()) { // begin transaction

            // get all sensors from database and cast them into a list so that they're actually fetched
            buttonDomains = new ArrayList<>(IteratorUtil.asCollection(buttonRepository.findAll()));

            // end transaction
            tx.success();
        }

        return buttonDomains;
    }


    /**
     * Delete all domains.
     */
    public void deleteAllDomains() {

        buttonRepository.deleteAll();
    }


    /**
     * Get a BrickletIO4 object, instantiate a new one if necessary.
     *
     * @param  buttonDomain  The bricklet's domain from the database.
     *
     * @return  The actual BrickletIO4 object.
     */
    public BrickletIO4 getButton(ButtonDomain buttonDomain) {

        if (buttonDomain == null) {
            return null;
        }

        LOG.debug("Setting up sensor {}.", buttonDomain.getName());

        if (!buttons.containsKey(buttonDomain)) {
            BrickletIO4 brickletIO4;

            try {
                // get the connection to the Brick, passing the BrickDomain and the calling object
                IPConnection ipConnection = brickService.getIPConnection(buttonDomain.getBrickDomain(), this);

                if (ipConnection != null) {
                    ButtonDomain sameSensor = domains.get(buttonDomain.getUid());

                    if (sameSensor != null) {
                        // If we already have a sensor with the same UID, fetch it.
                        brickletIO4 = buttons.get(sameSensor);
                    } else {
                        // Create a new Tinkerforge BrickletIO4 object with data from the database
                        brickletIO4 = new BrickletIO4(buttonDomain.getUid(), ipConnection);
                        domains.put(buttonDomain.getUid(), buttonDomain);
                    }

                    brickletIO4.setConfiguration(buttonDomain.getPins(), BrickletIO4.DIRECTION_IN, true); // set the configured pins as input with pull-up

                    short interrupts = brickletIO4.getInterrupt();

                    brickletIO4.setInterrupt((short) (buttonDomain.getPins() | interrupts)); // set interrupts for these pins, while respecting interrupts set earlier

                    brickletIO4.addInterruptListener(new ButtonListener(buttonDomain, relayService));
                } else {
                    LOG.error("Error setting up button {}: Brick {} not available.", buttonDomain.getName(),
                        buttonDomain.getBrickDomain().getHostname());

                    brickletIO4 = null;
                }
            } catch (TimeoutException | NotConnectedException e) {
                LOG.error("Error setting up button {}: {}", buttonDomain.getName(), e.toString());
                brickletIO4 = null; // if there is an error, we don't want to use this
            }

            if (brickletIO4 != null) {
                // add it to the HashMap
                buttons.put(buttonDomain, brickletIO4);
            }
        }

        LOG.debug("Finished setting up sensor {}.", buttonDomain.getName());

        return buttons.get(buttonDomain); // retrieve and return
    }


    /**
     * Clear the registry.
     */
    @Override
    public void clear() {

        buttons.clear();
        domains.clear();
    }
}
