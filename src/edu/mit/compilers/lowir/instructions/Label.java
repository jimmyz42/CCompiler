package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

public class Label extends Instruction {
	String name;
	
    public Label(String name) {
    	this.name = name;
    }

    public static Label create(String name) {
        return new Label(name);
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
		pw.println(name + ":");		
	}
}