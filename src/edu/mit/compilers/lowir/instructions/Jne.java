package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Memory;

public class Jne extends Instruction {
	Memory target;
    public Jne(Memory target) {
    	this.target = target;
    }

    public static Jne create(Memory target) {
    	return new Jne(target);
    }
    
	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
		pw.println(prefix + "jne " + target.toString());		
	}
}