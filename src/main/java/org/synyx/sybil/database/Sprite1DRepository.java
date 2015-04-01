package org.synyx.sybil.database;

import org.springframework.data.neo4j.repository.GraphRepository;

import org.springframework.stereotype.Repository;

import org.synyx.sybil.out.Sprite1D;


/**
 * Sprite1DRepository.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Repository // annotated so Spring finds it and can inject it.
public interface Sprite1DRepository extends GraphRepository<Sprite1D> {

    Sprite1D findByName(String name); // Spring builds a self-explanatory method out of this. MAGIC!
}