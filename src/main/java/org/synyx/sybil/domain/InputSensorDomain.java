package org.synyx.sybil.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import org.springframework.hateoas.core.Relation;

import org.synyx.sybil.in.SensorType;

import java.util.List;


/**
 * InputSensor domain. Persistence for the sensor configuration data, but not the actual objects.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@NodeEntity
@Relation(collectionRelation = "sensors")
public class InputSensorDomain {

    @GraphId
    private Long id;

    private String name;

    private String uid;

    private SensorType type;

    private List<String> outputs;

    @Fetch
    @RelatedTo(type = "IS_PART_OF")
    @JsonProperty("brick")
    private BrickDomain brickDomain;

    public InputSensorDomain() {
    }


    public InputSensorDomain(String name, String uid, SensorType type, List<String> outputs, BrickDomain brickDomain) {

        this.name = name;
        this.uid = uid;
        this.type = type;
        this.outputs = outputs;
        this.brickDomain = brickDomain;
    }

    public String getName() {

        return name;
    }


    public String getUid() {

        return uid;
    }


    public SensorType getType() {

        return type;
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

        InputSensorDomain that = (InputSensorDomain) o;

        return outputs.equals(that.outputs) && brickDomain.equals(that.brickDomain) && id.equals(that.id)
            && name.equals(that.name) && type.equals(that.type) && uid.equals(that.uid);
    }


    @Override
    public int hashCode() {

        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + uid.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + outputs.hashCode();
        result = 31 * result + brickDomain.hashCode();

        return result;
    }
}