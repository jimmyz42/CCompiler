package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Location;

public class Cmp extends Instruction {
    public Cmp(Location src, Location dest) {
    }

    public static Cmp create(Location src, Location dest) {
        return new Cmp(src, dest);
    }

	@Override
	public void print(PrintWriter pw, String prefix) {
		// TODO Auto-generated method stub
		
	}
}