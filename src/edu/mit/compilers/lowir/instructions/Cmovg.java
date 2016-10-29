package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Storage;

public class Cmovg extends Instruction {
	Storage src, dest;
	
    public Cmovg(Storage src, Storage dest) {
    	this.src = src;
    	this.dest = dest;
    }

    public static Cmovg create(Storage src, Storage dest) {
        return new Cmovg(src, dest);
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
        pw.print(prefix + "cmovgq");
        src.printAssembly(pw, " ");
        dest.printAssembly(pw, ", ");
        pw.println();
    }
}