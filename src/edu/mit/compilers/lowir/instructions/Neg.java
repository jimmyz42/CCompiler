package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Storage;
import edu.mit.compilers.lowir.StorageContainer;

/*
 * represent OR %src, %dest
 */
public class Neg extends Instruction {
	Storage src;
    public Neg(StorageContainer src) {
    	this.src = src;
    }
    
    public static Neg create(StorageContainer src) {
    	return new Neg(src);
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
        pw.print(prefix + "negq");
        src.printAssembly(pw, " ");
        pw.println();
	}
}