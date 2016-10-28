package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.BoolValue;
import edu.mit.compilers.lowir.Storage;

public class Cmovge extends Instruction {
	Storage src, dest;
    public Cmovge(Storage src, Storage dest) {
    	this.src = src;
    	this.dest = dest;
    }

    public static Cmovge create(BoolValue src, Storage dest) {
        return new Cmovge(src, dest);
    }

    public static Cmovge create(Storage src, Storage dest) {
        return new Cmovge(src, dest);
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
        pw.print(prefix + "cmovge");
        src.printAssembly(pw, " ");
        dest.printAssembly(pw, ", ");	
        pw.println();
	}
}