package org.synyx.sybil.bricklet.input.database;

import org.springframework.data.neo4j.repository.GraphRepository;

import org.springframework.stereotype.Repository;

import org.synyx.sybil.bricklet.input.SensorType;


/**
 * InputSensorRepository.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Repository // annotated so Spring finds it and can inject it.
public interface InputSensorRepository extends GraphRepository<InputSensorDomain> {

    InputSensorDomain findByName(String name); // Spring builds a self-explanatory method out of this. MAGIC!


    InputSensorDomain findByType(SensorType Type);
}
