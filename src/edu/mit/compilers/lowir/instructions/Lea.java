package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Storage;

public class Lea extends Instruction {
	Storage src, dest;
    public Lea(Storage src, Storage dest) {
    	this.src = src;
    	this.dest = dest;
    }
    
    public static Lea create(Storage src, Storage dest) {
        return new Lea(src, dest);
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
        pw.print(prefix + "lea");
        src.printAssembly(pw, " ");
        dest.printAssembly(pw, ", ");	
        pw.println();
	}
}
