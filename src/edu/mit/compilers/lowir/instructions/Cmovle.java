package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Location;

public class Cmovle extends Instruction {
    public Cmovle(Object src, Object dest) {
    }

    public static Cmovle create(Boolean src, Location dest) {
        return new Cmovle(src, dest.getName());
    }

    public static Cmovle create(Location src, Location dest) {
        return new Cmovle(src.getName(), dest.getName());
    }

	@Override
	public void print(PrintWriter pw, String prefix) {
		// TODO Auto-generated method stub
		
	}
}