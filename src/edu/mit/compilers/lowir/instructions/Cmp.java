package edu.mit.compilers.lowir.instructions;

import edu.mit.compilers.lowir.Location;

public class Cmp extends Instruction {
    public Cmp(Location src, Location dest) {
    }

    public static Cmp create(Location src, Location dest) {
        return new Cmp(src, dest);
    }
}