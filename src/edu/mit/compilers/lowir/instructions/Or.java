package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Storage;

/*
 * represent OR %src, %dest
 */
public class Or extends Instruction {
	Storage src, dest;
    public Or(Storage src, Storage dest) {
    	this.src = src;
    	this.dest = dest;
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
        pw.print(prefix + "or");
        src.printAssembly(pw, " ");
        dest.printAssembly(pw, ", ");
        pw.println();
	}
}