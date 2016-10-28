package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.BoolValue;
import edu.mit.compilers.lowir.Storage;

public class Cmovg extends Instruction {
	Storage src, dest;
	
    public Cmovg(Storage src, Storage dest) {
    	this.src = src;
    	this.dest = dest;
    }

    public static Cmovg create(BoolValue src, Storage dest) {
        return new Cmovg(src, dest);
    }

    public static Cmovg create(Storage src, Storage dest) {
        return new Cmovg(src, dest);
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
        pw.print(prefix + "cmovg");
        src.printAssembly(pw, " ");
        dest.printAssembly(pw, ", ");
        pw.println();
    }
}