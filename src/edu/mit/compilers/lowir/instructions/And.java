package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Location;

/*
 * represents AND %src, %dest
 */
public class And extends Instruction {
    public And(Location src, Location dest) {
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
		// TODO Auto-generated method stub
	}
}