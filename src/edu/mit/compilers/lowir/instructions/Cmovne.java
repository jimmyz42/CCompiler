package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Location;

public class Cmovne extends Instruction {
    public Cmovne(Object src, Object dest) {
    }

    public static Cmovne create(Boolean src, Location dest) {
        return new Cmovne(src, dest.getName());
    }

    public static Cmovne create(Location src, Location dest) {
        return new Cmovne(src.getName(), dest.getName());
    }

	@Override
	public void print(PrintWriter pw, String prefix) {
		// TODO Auto-generated method stub
		
	}
}