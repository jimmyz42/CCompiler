package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Location;

public class Cmovl extends Instruction {
	Object src, dest;
    public Cmovl(Object src, Object dest) {
    	this.src = src; 
    	this.dest = dest;
    }

    public static Cmovl create(Boolean src, Location dest) {
        return new Cmovl(src, dest.getName());
    }

    public static Cmovl create(Location src, Location dest) {
        return new Cmovl(src.getName(), dest.getName());
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
        pw.print(prefix + "cmovl " + src.toString() + ", " + dest.toString());				
	}
}