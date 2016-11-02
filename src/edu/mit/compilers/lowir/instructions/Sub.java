package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Storage;

public class Sub extends Instruction {
	Storage src, dest;
    public Sub(Storage src, Storage dest) {
    	this.src = src; 
    	this.dest = dest;
    }
    
    public static Sub create(Storage src, Storage dest) {
    	return new Sub(src, dest);
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
        pw.print(prefix + "subq");
        src.printAssembly(pw, " ");
        dest.printAssembly(pw, ", ");
        pw.println();
    }
}