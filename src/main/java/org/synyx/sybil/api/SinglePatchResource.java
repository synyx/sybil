package org.synyx.sybil.api;

import java.util.List;


/**
 * SinglePatchResource.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class SinglePatchResource {

    private String action;
    private String target;
    private List<String> values;

    public String getAction() {

        return action;
    }


    public void setAction(String action) {

        this.action = action;
    }


    public String getTarget() {

        return target;
    }


    public void setTarget(String target) {

        this.target = target;
    }


    public List<String> getValues() {

        return values;
    }


    public void setValues(List<String> values) {

        this.values = values;
    }
}
