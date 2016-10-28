package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Location;

public class Cmovge extends Instruction {
    public Cmovge(Object src, Object dest) {
    }

    public static Cmovge create(Boolean src, Location dest) {
        return new Cmovge(src, dest.getName());
    }

    public static Cmovge create(Location src, Location dest) {
        return new Cmovge(src.getName(), dest.getName());
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
		// TODO Auto-generated method stub
		
	}
}