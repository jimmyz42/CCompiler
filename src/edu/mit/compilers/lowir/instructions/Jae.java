package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Memory;

public class Jae extends Instruction {
	Memory target;
    public Jae(Memory target) {
    	this.target = target;
    }

    public static Jae create(Memory target) {
    	return new Jae(target);
    }
    
	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
        pw.print(prefix + "jae");
        target.printAssembly(pw, " ");
        pw.println();
	}
}
