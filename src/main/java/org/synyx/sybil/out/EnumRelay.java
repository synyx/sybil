package org.synyx.sybil.out;

/**
 * Relay enum.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public enum EnumRelay {

    ONE((short) 1),
    TWO((short) 2);

    private short value;

    EnumRelay(short value) {

        this.value = value;
    }

    public short getValue() {

        return value;
    }
}
