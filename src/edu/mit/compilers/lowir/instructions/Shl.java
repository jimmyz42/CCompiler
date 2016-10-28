package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Location;

public class Shl extends Instruction {
	Location reg;
    public Shl(Location reg) {
    	this.reg = reg;
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
		pw.println(prefix + "shl " + reg.toString());		
	}
}