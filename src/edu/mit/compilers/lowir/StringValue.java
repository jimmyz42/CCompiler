package edu.mit.compilers.lowir;

import java.io.PrintWriter;

public class StringValue extends Storage{

	public StringValue(String name) {
		super(name);
	}
	
	public static StringValue create(String name){
		return new StringValue(name);
	}
	
	@Override 
	public void printAssembly(PrintWriter pw, String prefix){
		pw.println("." + "stringlabel:"); //TODO: get unique labels 
		pw.println(prefix + ".string " + "\"" + this.getName() + "\"" );
	}

}
