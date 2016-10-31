package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Register;
import edu.mit.compilers.lowir.Storage;
import edu.mit.compilers.lowir.StorageContainer;

public class Lea extends Instruction {
	Storage src, dest;
    public Lea(StorageContainer src, Register dest) {
    	this.src = src;
    	this.dest = dest;
    }
    
    public static Lea create(StorageContainer src, Register dest) {
        return new Lea(src, dest);
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
        pw.print(prefix + "leaq");
        src.printAssembly(pw, " ");
        dest.printAssembly(pw, ", ");	
        pw.println();
	}
}
