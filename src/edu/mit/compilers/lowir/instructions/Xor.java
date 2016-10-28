package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Location;

public class Xor extends Instruction {
	Location src, dest;
    public Xor(Location src, Location dest) {
    	this.src = src;
    	this.dest = dest;
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
		pw.println(prefix + "xor" + src.toString() + ", " + dest.toString());		
	}
}