package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Location;

/*
 * represent IMUL %src, %dest
 *
 * multiplies dest by src
 */
public class Imul extends Instruction {
	Location src, dest;
    public Imul(Location src, Location dest) {
    	this.src = src;
    	this.dest = dest;
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
		pw.println(prefix + "imul " + src.toString() + ", " + dest.toString());		
	}
}