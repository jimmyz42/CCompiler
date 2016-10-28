package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Location;

public class Sub extends Instruction {
	Location src, dest;
    public Sub(Location src, Location dest) {
    	this.src = src; 
    	this.dest = dest;
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
		pw.println(prefix + "sub" + src.toString() + ", " + dest.toString());		
	}
}