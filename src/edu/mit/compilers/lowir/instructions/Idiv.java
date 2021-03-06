package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Storage;

public class Idiv extends Instruction {
	Storage divisor;
    public Idiv(Storage divisor) {
    	this.divisor = divisor;
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
        pw.print(prefix + "idivq");
        divisor.printAssembly(pw, " ");
        pw.println();
    }
}