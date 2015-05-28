package org.synyx.sybil.bricklet.input.button.database;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import org.springframework.hateoas.core.Relation;

import org.synyx.sybil.brick.database.BrickDomain;

import java.util.List;


/**
 * Button domain. Persistence for the button configuration data, but not the actual objects.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@NodeEntity
@Relation(collectionRelation = "buttons")
public class ButtonDomain {

    @GraphId
    private Long id;

    private String name;

    private String uid;

    private short pins;

    private List<String> outputs;

    @Fetch
    @RelatedTo(type = "IS_PART_OF")
    @JsonProperty("brick")
    private BrickDomain brickDomain;

    public ButtonDomain() {
    }


    public ButtonDomain(String name, String uid, short pins, List<String> outputs, BrickDomain brickDomain) {

        this.name = name;
        this.uid = uid;
        this.pins = pins;
        this.outputs = outputs;
        this.brickDomain = brickDomain;
    }

    public String getName() {

        return name;
    }


    public String getUid() {

        return uid;
    }


    public short getPins() {

        return pins;
    }


    public List<String> getOutputs() {

        return outputs;
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

        ButtonDomain that = (ButtonDomain) o;

        return pins == that.pins && brickDomain.equals(that.brickDomain) && id.equals(that.id) && name.equals(that.name)
            && outputs.equals(that.outputs) && uid.equals(that.uid);
    }


    @Override
    public int hashCode() {

        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + uid.hashCode();
        result = 31 * result + (int) pins;
        result = 31 * result + outputs.hashCode();
        result = 31 * result + brickDomain.hashCode();

        return result;
    }
}
