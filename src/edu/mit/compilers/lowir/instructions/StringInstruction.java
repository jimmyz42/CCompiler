package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.StringValue;

public class StringInstruction extends Instruction{
	String label, text;
	public StringInstruction(String label, String text) {
		this.label = label;
		this.text = text;
	}
	
	@Override 
	public void printAssembly(PrintWriter pw, String prefix){
		pw.println("." + label + ":");
		pw.println(prefix + ".string " + text);
	}

}
