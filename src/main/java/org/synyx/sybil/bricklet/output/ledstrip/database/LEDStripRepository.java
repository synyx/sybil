package org.synyx.sybil.bricklet.output.ledstrip.database;

import org.springframework.data.neo4j.repository.GraphRepository;

import org.springframework.stereotype.Repository;


/**
 * LEDStripRepository.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Repository
public interface LEDStripRepository extends GraphRepository<LEDStripDomain> {

    LEDStripDomain findByName(String name);
}
