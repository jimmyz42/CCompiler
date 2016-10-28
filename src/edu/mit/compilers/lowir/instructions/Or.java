package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Location;

/*
 * represent OR %src, %dest
 */
public class Or extends Instruction {
    public Or(Location src, Location dest) {
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
		// TODO Auto-generated method stub
		
	}
}