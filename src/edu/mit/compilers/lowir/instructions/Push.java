package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Storage;

public class Push extends Instruction {
	Storage src;
    public Push(Storage src) {
    	this.src = src;
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
		pw.println(prefix + "push " + src.toString());		
	}
}