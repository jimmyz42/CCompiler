package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Storage;

public class Cmovl extends Instruction {
	Storage src, dest;
    public Cmovl(Storage src, Storage dest) {
    	this.src = src; 
    	this.dest = dest;
    }
    
    public static Cmovl create(Storage src, Storage dest) {
        return new Cmovl(src, dest);
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
        pw.print(prefix + "cmovl");
        src.printAssembly(pw, " ");
        dest.printAssembly(pw, ", ");
        pw.println();
	}
}