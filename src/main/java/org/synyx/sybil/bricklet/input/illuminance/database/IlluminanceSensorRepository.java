package org.synyx.sybil.bricklet.input.illuminance.database;

import org.springframework.data.neo4j.repository.GraphRepository;

import org.springframework.stereotype.Repository;


/**
 * IlluminanceSensorRepository.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Repository
public interface IlluminanceSensorRepository extends GraphRepository<IlluminanceSensorDomain> {

    IlluminanceSensorDomain findByName(String name);
}
