package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Storage;

public class Call extends Instruction {
	Storage target;
    public Call(Storage target) {
    	this.target = target;
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
        pw.println(prefix + "call " + target);		
	}
}