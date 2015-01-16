package org.synyx.sybil.database;

import org.springframework.data.neo4j.repository.GraphRepository;

import org.springframework.stereotype.Repository;

import org.synyx.sybil.domain.OutputLEDStripDomain;


/**
 * OutputLEDStripRepository.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Repository
public interface OutputLEDStripRepository extends GraphRepository<OutputLEDStripDomain> {

    OutputLEDStripDomain findByName(String name);
}
