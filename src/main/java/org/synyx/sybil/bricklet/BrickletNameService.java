package org.synyx.sybil.bricklet;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;


/**
 * BrickletNameRegistry.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Service
public class BrickletNameService implements BrickletService {

    private Set<String> brickletNames = new HashSet<>();

    @Override
    public void clear() {

        brickletNames.clear();
    }


    public boolean contains(String name) {

        return brickletNames.contains(name);
    }


    public void add(String name) {

        brickletNames.add(name);
    }
}
