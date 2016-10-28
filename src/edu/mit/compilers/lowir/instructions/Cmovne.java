package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Storage;

public class Cmovne extends Instruction {
	Object src, dest;
    public Cmovne(Object src, Object dest) {
    	this.src = src;
    	this.dest = dest;
    }

    public static Cmovne create(Boolean src, Storage dest) {
        return new Cmovne(src, dest.getName());
    }

    public static Cmovne create(Storage src, Storage dest) {
        return new Cmovne(src.getName(), dest.getName());
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
        pw.println(prefix + "cmovne " + src.toString() + ", " + dest.toString());	
	}
}