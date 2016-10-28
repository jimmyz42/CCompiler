package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Location;

public class Push extends Instruction {
	Location src;
    public Push(Location src) {
    	this.src = src;
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
		pw.println(prefix + "push " + src.toString());		
	}
}