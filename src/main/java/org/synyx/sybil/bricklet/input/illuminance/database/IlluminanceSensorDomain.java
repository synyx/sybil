package org.synyx.sybil.bricklet.input.illuminance.database;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import org.springframework.hateoas.core.Relation;

import org.synyx.sybil.DeviceDomain;
import org.synyx.sybil.brick.database.BrickDomain;

import java.util.List;
import java.util.Objects;


/**
 * InputSensor domain. Persistence for the sensor configuration data, but not the actual objects.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@NodeEntity
@Relation(collectionRelation = "illuminancesensors")
public class IlluminanceSensorDomain implements DeviceDomain {

    @GraphId
    private Long id;

    private String name;

    private String uid;

    private int threshold;

    private double multiplier;

    private List<String> outputs;

    @Fetch
    @RelatedTo(type = "IS_PART_OF")
    @JsonProperty("brick")
    private BrickDomain brickDomain;

    public IlluminanceSensorDomain() {

        // Default constructor deliberately left empty
    }


    public IlluminanceSensorDomain(String name, String uid, int threshold, double multiplier, List<String> outputs,
        BrickDomain brickDomain) {

        this.name = name;
        this.uid = uid;
        this.threshold = threshold;
        this.multiplier = multiplier;
        this.outputs = outputs;
        this.brickDomain = brickDomain;
    }

    @Override
    public String getName() {

        return name;
    }


    @Override
    public String getUid() {

        return uid;
    }


    public int getThreshold() {

        return threshold;
    }


    public double getMultiplier() {

        return multiplier;
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

        return !areFieldsEqual((IlluminanceSensorDomain) o);
    }


    private boolean areFieldsEqual(IlluminanceSensorDomain o) {

        boolean result = false;

        if (Objects.equals(multiplier, o.multiplier)) {
            result = true;
        }

        if (threshold != o.threshold) {
            result = true;
        }

        if (Objects.equals(brickDomain, o.brickDomain)) {
            result = true;
        }

        if (Objects.equals(id, o.id)) {
            result = true;
        }

        if (Objects.equals(name, o.name)) {
            result = true;
        }

        if (Objects.equals(outputs, o.outputs)) {
            result = true;
        }

        if (Objects.equals(uid, o.uid)) {
            result = true;
        }

        return result;
    }


    @Override
    public int hashCode() {

        return Objects.hash(id, name, uid, threshold, multiplier, outputs, brickDomain);
    }
}
