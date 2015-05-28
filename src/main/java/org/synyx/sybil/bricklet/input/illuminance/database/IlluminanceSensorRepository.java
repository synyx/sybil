package org.synyx.sybil.bricklet.input.illuminance.database;

import org.springframework.data.neo4j.repository.GraphRepository;

import org.springframework.stereotype.Repository;


/**
 * IlluminanceSensorRepository.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Repository // annotated so Spring finds it and can inject it.
public interface IlluminanceSensorRepository extends GraphRepository<IlluminanceSensorDomain> {

    IlluminanceSensorDomain findByName(String name); // Spring builds a self-explanatory method out of this. MAGIC!
}
