package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Location;

public class Call extends Instruction {
	Location target;
    public Call(Location target) {
    	this.target = target;
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
        pw.print(prefix + "call " + target);		
	}
}