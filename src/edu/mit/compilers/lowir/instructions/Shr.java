package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Location;

public class Shr extends Instruction {
	Location reg;
    public Shr(Location reg) {
    	this.reg = reg;
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
		pw.println(prefix + "shr " + reg.toString());		
	}
}