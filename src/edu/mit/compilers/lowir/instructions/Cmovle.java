package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Storage;

public class Cmovle extends Instruction {
	Storage src, dest;
    public Cmovle(Storage src, Storage dest) {
    	this.src = src;
    	this.dest = dest;
    }

//    public static Cmovle create(Boolean src, Storage dest) {
//        return new Cmovle(src, dest.getName());
//    }
//
//    public static Cmovle create(Storage src, Storage dest) {
//        return new Cmovle(src.getName(), dest.getName());
//    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
        pw.print(prefix + "cmovle");
        src.printAssembly(pw, " ");
        dest.printAssembly(pw, ", ");		
        pw.println();
	}
}