package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Storage;

public class Cmovle extends Instruction {
	Object src, dest;
    public Cmovle(Object src, Object dest) {
    	this.src = src;
    	this.dest = dest;
    }

    public static Cmovle create(Boolean src, Storage dest) {
        return new Cmovle(src, dest.getName());
    }

    public static Cmovle create(Storage src, Storage dest) {
        return new Cmovle(src.getName(), dest.getName());
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
        pw.println(prefix + "cmovle " + src.toString() + ", " + dest.toString());		
	}
}