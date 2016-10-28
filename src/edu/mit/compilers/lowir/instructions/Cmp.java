package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Location;

public class Cmp extends Instruction {
	Location src, dest;
    public Cmp(Location src, Location dest) {
    	this.src = src;
    	this.dest = dest;
    }

    public static Cmp create(Location src, Location dest) {
        return new Cmp(src, dest);
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
		pw.println(prefix + "cmp " + src.toString() + ", " + dest.toString());
	}
}