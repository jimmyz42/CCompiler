package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Storage;

/*
 * represent IMUL %src, %dest
 *
 * multiplies dest by src
 */
public class Imul extends Instruction {
	Storage src, dest;
    public Imul(Storage src, Storage dest) {
    	this.src = src;
    	this.dest = dest;
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
        pw.print(prefix + "imul");
        src.printAssembly(pw, " ");
        dest.printAssembly(pw, ", ");
        pw.println();
	}
}