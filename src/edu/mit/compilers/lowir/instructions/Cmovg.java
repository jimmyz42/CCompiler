package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Location;

public class Cmovg extends Instruction {
	Object src, dest;
	
    public Cmovg(Object src, Object dest) {
    	this.src = src;
    	this.dest = dest;
    }

    public static Cmovg create(Boolean src, Location dest) {
        return new Cmovg(src, dest.getName());
    }

    public static Cmovg create(Location src, Location dest) {
        return new Cmovg(src.getName(), dest.getName());
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
        pw.println(prefix + "cmovg " + src.toString() + ", " + dest.toString());				
	}
}