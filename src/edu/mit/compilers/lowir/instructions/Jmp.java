package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Memory;

public class Jmp extends Instruction {
	Memory target;
    public Jmp(Memory target) {
    	this.target = target;
    }

    public static Jmp create(Memory target) {
    	return new Jmp(target);
    }
    
	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
        pw.print(prefix + "jmp");
        target.printAssembly(pw, " ");
        pw.println();
	}
}