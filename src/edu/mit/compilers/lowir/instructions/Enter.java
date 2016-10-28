package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

public class Enter extends Instruction {
	private long numVars;
	
    public Enter(long numVars) {
    	this.numVars = numVars;
    }
    
    public static Enter create(long numVars) {
    	return new Enter(numVars);
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
		pw.println(prefix + "enter $(8*" + numVars + "), $0");
		pw.println();
	}
}