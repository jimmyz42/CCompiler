package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Storage;

public class Add extends Instruction {
	Storage src;
	Storage dest;
	
    public Add(Storage src, Storage dest) {
    	this.src = src; 
    	this.dest = dest;
    }

    @Override
    public void printAssembly(PrintWriter pw, String prefix) {
        pw.print(prefix + "addq");
        src.printAssembly(pw, " ");
        dest.printAssembly(pw, ", ");
        pw.println();
    }
}