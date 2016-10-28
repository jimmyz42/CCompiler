package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Storage;

public class Cmp extends Instruction {
	Storage src, dest;
    public Cmp(Storage src, Storage dest) {
    	this.src = src;
    	this.dest = dest;
    }

    public static Cmp create(Storage src, Storage dest) {
        return new Cmp(src, dest);
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
		pw.println(prefix + "cmp " + src.toString() + ", " + dest.toString());
	}
}