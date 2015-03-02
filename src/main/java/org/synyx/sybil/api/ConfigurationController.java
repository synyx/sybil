package org.synyx.sybil.api;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

import org.neo4j.helpers.collection.IteratorUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import org.synyx.sybil.database.BrickRepository;
import org.synyx.sybil.database.OutputLEDStripRepository;
import org.synyx.sybil.domain.BrickDomain;

import java.util.ArrayList;
import java.util.List;


/**
 * ConfigurationController.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@RestController
@RequestMapping("/configuration")
public class ConfigurationController {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationController.class);

    private BrickRepository brickRepository;
    private OutputLEDStripRepository outputLEDStripRepository;
    private GraphDatabaseService graphDatabaseService;

    @Autowired
    public ConfigurationController(BrickRepository brickRepository, OutputLEDStripRepository outputLEDStripRepository,
        GraphDatabaseService graphDatabaseService) {

        this.brickRepository = brickRepository;
        this.outputLEDStripRepository = outputLEDStripRepository;
        this.graphDatabaseService = graphDatabaseService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<BrickDomain> bricks() {

        LOG.info("GET /configuration");

        List<BrickDomain> brickDomains;

        try(Transaction tx = graphDatabaseService.beginTx()) { // begin transaction

            // get all Bricks from database and cast them into a list so that they're actually fetched
            brickDomains = new ArrayList<>(IteratorUtil.asCollection(brickRepository.findAll()));

            // end transaction
            tx.success();
        }

        return brickDomains;
    }
}
