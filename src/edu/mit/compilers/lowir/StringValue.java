package edu.mit.compilers.lowir;

import java.io.PrintWriter;

public class StringValue extends Storage{

	public StringValue(String name) {
		super(name);
	}
	
	@Override 
	public void printAssembly(PrintWriter pw, String prefix){
		pw.println("." + "stringlabel:"); //TODO: get unique labels 
		pw.println("    " + ".string " + "\"" + this.getName() + "\"" );
	}

}
