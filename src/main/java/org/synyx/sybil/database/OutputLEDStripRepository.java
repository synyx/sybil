package org.synyx.sybil.database;

import org.springframework.data.neo4j.repository.GraphRepository;

import org.springframework.stereotype.Repository;

import org.synyx.sybil.domain.OutputLEDStrip;


/**
 * OutputLEDStripRepository.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Repository
public interface OutputLEDStripRepository extends GraphRepository<OutputLEDStrip> {

    OutputLEDStrip findByName(String name);
}
