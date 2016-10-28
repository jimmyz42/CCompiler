package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Storage;

/*
 * represents AND %src, %dest
 */
public class And extends Instruction {
	Storage src, dest;
	
    public And(Storage src, Storage dest) {
    	this.src = src; 
    	this.dest = dest;
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
        pw.println(prefix + "and " + src + ", " + dest);
	}
}