package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Storage;

public class Xor extends Instruction {
	Storage src, dest;
    public Xor(Storage src, Storage dest) {
    	this.src = src;
    	this.dest = dest;
    }
    
    public static Xor create(Storage src, Storage dest)  {
    	return new Xor(src, dest);
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
        pw.print(prefix + "xorq");
        src.printAssembly(pw, " ");
        dest.printAssembly(pw, ", ");
        pw.println();		
	}
}