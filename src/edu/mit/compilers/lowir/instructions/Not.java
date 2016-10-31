package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Storage;
import edu.mit.compilers.lowir.StorageContainer;

/*
 * represent OR %src, %dest
 */
public class Not extends Instruction {
	Storage src;
    public Not(StorageContainer src) {
    	this.src = src;
    }
    
    public static Not create(StorageContainer src) {
    	return new Not(src);
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
        pw.print(prefix + "notq");
        src.printAssembly(pw, " ");
        pw.println();
	}
}