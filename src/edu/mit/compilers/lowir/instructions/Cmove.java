package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Location;

public class Cmove extends Instruction {
	Object src, dest;
	
    public Cmove(Object src, Object dest) {
    	this.src = src;
    	this.dest = dest;
    }

    public static Cmove create(Boolean src, Location dest) {
        return new Cmove(src, dest.getName());
    }

    public static Cmove create(Location src, Location dest) {
        return new Cmove(src.getName(), dest.getName());
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
        pw.println(prefix + "cmove " + src.toString() + ", " + dest.toString());		
	}
}