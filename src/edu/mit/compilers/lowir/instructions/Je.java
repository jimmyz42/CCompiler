package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Memory;

public class Je extends Instruction {
	Memory target;
    public Je(Memory target) {
    	this.target = target;
    }

    public static Je create(Memory target) {
    	return new Je(target);
    }
    
	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
		pw.println(prefix + "je " + target.toString());		
	}
}