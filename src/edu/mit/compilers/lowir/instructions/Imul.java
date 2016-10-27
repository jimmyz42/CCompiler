package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Location;

/*
 * represent IMUL %src, %dest
 *
 * multiplies dest by src
 */
public class Imul extends Instruction {
    public Imul(Location src, Location dest) {
    }

	@Override
	public void print(PrintWriter pw, String prefix) {
		// TODO Auto-generated method stub
		
	}
}