package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Location;

public class Cmovle extends Instruction {
	Object src, dest;
    public Cmovle(Object src, Object dest) {
    	this.src = src;
    	this.dest = dest;
    }

    public static Cmovle create(Boolean src, Location dest) {
        return new Cmovle(src, dest.getName());
    }

    public static Cmovle create(Location src, Location dest) {
        return new Cmovle(src.getName(), dest.getName());
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
        pw.println(prefix + "cmovle " + src.toString() + ", " + dest.toString());		
	}
}