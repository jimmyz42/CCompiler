package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Storage;

public class Shl extends Instruction {
	Storage reg;
    public Shl(Storage reg) {
    	this.reg = reg;
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
        pw.print(prefix + "shl");
        reg.printAssembly(pw, " ");
        pw.println();	
	}
}