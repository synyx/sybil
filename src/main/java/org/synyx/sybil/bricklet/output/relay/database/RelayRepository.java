package org.synyx.sybil.bricklet.output.relay.database;

import org.springframework.data.neo4j.repository.GraphRepository;

import org.springframework.stereotype.Repository;


/**
 * RelayRepository.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Repository // annotated so Spring finds it and can inject it.
public interface RelayRepository extends GraphRepository<RelayDomain> {

    RelayDomain findByName(String name); // Spring builds a self-explanatory method out of this. MAGIC!
}
