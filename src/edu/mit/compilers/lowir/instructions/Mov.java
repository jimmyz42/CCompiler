package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Storage;

public class Mov extends Instruction {
	Storage src, dest;
    public Mov(Storage src, Storage dest) {
    	this.src = src;
    	this.dest = dest;
    }
    
    public static Mov create(Storage src, Storage dest) {
        return new Mov(src, dest);
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
        pw.print(prefix + "movq");
        src.printAssembly(pw, " ");
        dest.printAssembly(pw, ", ");	
        pw.println();
	}
}
