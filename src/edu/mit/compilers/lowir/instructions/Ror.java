package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Storage;

public class Ror extends Instruction {
	Storage src, dest;
    public Ror(Storage src, Storage dest) {
    	this.src = src; 
    	this.dest = dest;
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
        pw.print(prefix + "ror");
        src.printAssembly(pw, " ");
        dest.printAssembly(pw, ", ");
        pw.println();
	}
}