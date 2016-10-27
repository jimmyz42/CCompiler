package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Location;

public class Cmovg extends Instruction {
    public Cmovg(Object src, Object dest) {
    }

    public static Cmovg create(Boolean src, Location dest) {
        return new Cmovg(src, dest.getName());
    }

    public static Cmovg create(Location src, Location dest) {
        return new Cmovg(src.getName(), dest.getName());
    }

	@Override
	public void print(PrintWriter pw, String prefix) {
		// TODO Auto-generated method stub
		
	}
}