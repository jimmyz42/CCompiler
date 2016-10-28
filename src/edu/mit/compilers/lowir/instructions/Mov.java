package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Location;

public class Mov extends Instruction {
    public Mov(Object src, Object dest) {
    }

    public static Mov create(Boolean src, Location dest) {
        return new Mov(src, dest.getName());
    }

    public static Mov create(Location src, Location dest) {
        return new Mov(src.getName(), dest.getName());
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
		// TODO Auto-generated method stub
		
	}
}