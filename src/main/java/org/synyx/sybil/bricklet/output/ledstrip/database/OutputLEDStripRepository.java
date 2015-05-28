package org.synyx.sybil.bricklet.output.ledstrip.database;

import org.springframework.data.neo4j.repository.GraphRepository;

import org.springframework.stereotype.Repository;


/**
 * OutputLEDStripRepository.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Repository // annotated so Spring finds it and can inject it.
public interface OutputLEDStripRepository extends GraphRepository<OutputLEDStripDomain> {

    OutputLEDStripDomain findByName(String name); // Spring builds a self-explanatory method out of this. MAGIC!
}
