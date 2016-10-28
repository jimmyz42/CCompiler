package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Storage;

public class Pop extends Instruction {
	Storage dest;
    public Pop(Storage dest) {
    	this.dest = dest;
    }

    public static Pop create(Storage dest){
    	return new Pop(dest);
    }
    
	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
		pw.println(prefix + "pop " + dest);		
	}
}