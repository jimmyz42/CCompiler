package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

public class Directive extends Instruction {
	String text;
	
    public Directive(String text) {
    	this.text = text;
    }

    public static Directive create(String text) {
        return new Directive(text);
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
		pw.println(prefix + text);		
	}
}