package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Storage;

public class Shr extends Instruction {
	Storage reg;
    public Shr(Storage reg) {
    	this.reg = reg;
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
        pw.print(prefix + "mov");
        reg.printAssembly(pw, " ");
        pw.println();		
	}
}