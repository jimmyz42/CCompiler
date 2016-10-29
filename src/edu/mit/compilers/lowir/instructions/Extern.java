package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Storage;

public class Extern extends Instruction {
	String name;
	
    public Extern(String name) {
    	this.name = name;
    }

    public static Extern create(String name) {
        return new Extern(name);
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
		pw.println("extern " + name + ":proc");		
	}
}