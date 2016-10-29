package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Storage;

public class Cmove extends Instruction {
	Storage src, dest;
	
    public Cmove(Storage src, Storage dest) {
    	this.src = src;
    	this.dest = dest;
    }

    public static Cmove create(Storage src, Storage dest) {
        return new Cmove(src, dest);
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
        pw.print(prefix + "cmovel");
        src.printAssembly(pw, " ");
        dest.printAssembly(pw, ", ");
        pw.println();
	}
}