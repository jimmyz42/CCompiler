package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Location;

public class Add extends Instruction {
    public Add(Location src, Location dest) {
    }

    @Override
    public void print(PrintWriter pw, String prefix) {
        pw.print(prefix + toString());
    }
}