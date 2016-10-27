package edu.mit.compilers.lowir.instructions;

import edu.mit.compilers.lowir.Location;

public class Cmove extends Instruction {
    public Cmove(Object src, Object dest) {
    }

    public static Cmove create(Boolean src, Location dest) {
        return new Cmove(src, dest.getName());
    }

    public static Cmove create(Location src, Location dest) {
        return new Cmove(src.getName(), dest.getName());
    }
}