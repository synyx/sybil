package org.synyx.sybil.bricklet.output.relay.database;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import org.springframework.hateoas.core.Relation;

import org.synyx.sybil.brick.database.BrickDomain;


/**
 * Relay domain. Persistence for the relay configuration data, but not the actual objects.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@NodeEntity
@Relation(collectionRelation = "relays")
public class RelayDomain {

    @GraphId
    private Long id;

    private String name;

    private String uid;

    @Fetch
    @RelatedTo(type = "IS_PART_OF")
    @JsonProperty("brick")
    private BrickDomain brickDomain;

    public RelayDomain() {
    }


    public RelayDomain(String name, String uid, BrickDomain brickDomain) {

        this.name = name;
        this.uid = uid;
        this.brickDomain = brickDomain;
    }

    public String getName() {

        return name;
    }


    public String getUid() {

        return uid;
    }


    public BrickDomain getBrickDomain() {

        return brickDomain;
    }


    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RelayDomain that = (RelayDomain) o;

        return id.equals(that.id) && name.equals(that.name) && uid.equals(that.uid)
            && brickDomain.equals(that.brickDomain);
    }


    @Override
    public int hashCode() {

        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + uid.hashCode();
        result = 31 * result + brickDomain.hashCode();

        return result;
    }
}
