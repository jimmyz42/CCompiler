package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Location;

/*
 * represents AND %src, %dest
 */
public class And extends Instruction {
	Location src, dest;
	
    public And(Location src, Location dest) {
    	this.src = src; 
    	this.dest = dest;
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
        pw.println(prefix + "and " + src + ", " + dest);
	}
}