package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Storage;

public class Xor extends Instruction {
	Storage src, dest;
    public Xor(Storage src, Storage dest) {
    	this.src = src;
    	this.dest = dest;
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
		pw.println(prefix + "xor" + src.toString() + ", " + dest.toString());		
	}
}