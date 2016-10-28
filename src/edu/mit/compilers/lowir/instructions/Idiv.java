package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Location;

public class Idiv extends Instruction {
	Location divisor;
    public Idiv(Location divisor) {
    	this.divisor = divisor;
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
		pw.println(prefix + "idiv " + divisor.toString());		
	}
}