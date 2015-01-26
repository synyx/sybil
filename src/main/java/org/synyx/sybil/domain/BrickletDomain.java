package org.synyx.sybil.domain;

import org.springframework.data.neo4j.annotation.NodeEntity;


/**
 * BrickletDomain.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@NodeEntity
public interface BrickletDomain {

    String getName();


    String getUid();


    BrickDomain getBrickDomain();


    String getType();
}
