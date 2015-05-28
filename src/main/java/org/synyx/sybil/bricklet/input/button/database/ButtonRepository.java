package org.synyx.sybil.bricklet.input.button.database;

import org.springframework.data.neo4j.repository.GraphRepository;

import org.springframework.stereotype.Repository;


/**
 * ButtonRepository.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Repository // annotated so Spring finds it and can inject it.
public interface ButtonRepository extends GraphRepository<ButtonDomain> {

    ButtonDomain findByName(String name); // Spring builds a self-explanatory method out of this. MAGIC!
}
