package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Location;

public class Cmovne extends Instruction {
	Object src, dest;
    public Cmovne(Object src, Object dest) {
    	this.src = src;
    	this.dest = dest;
    }

    public static Cmovne create(Boolean src, Location dest) {
        return new Cmovne(src, dest.getName());
    }

    public static Cmovne create(Location src, Location dest) {
        return new Cmovne(src.getName(), dest.getName());
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
        pw.print(prefix + "cmovne " + src.toString() + ", " + dest.toString());		
		
	}
}