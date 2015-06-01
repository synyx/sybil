package org.synyx.sybil.api;

import java.util.List;


/**
 * PatchResource.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class PatchResource {

    private List<SinglePatchResource> patches;

    public List<SinglePatchResource> getPatches() {

        return patches;
    }


    public void setPatches(List<SinglePatchResource> patches) {

        this.patches = patches;
    }
}
