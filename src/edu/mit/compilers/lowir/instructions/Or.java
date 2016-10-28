package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Location;

/*
 * represent OR %src, %dest
 */
public class Or extends Instruction {
	Location src, dest;
    public Or(Location src, Location dest) {
    	this.src = src;
    	this.dest = dest;
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
		pw.println(prefix + "or " + src.toString() + ", " + dest.toString());		
	}
}