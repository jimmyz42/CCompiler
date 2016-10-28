package edu.mit.compilers.lowir;

import java.io.PrintWriter;

public class BoolValue extends Storage{

	public BoolValue(String name) {
		super(name);
		if (name.equals("true")){
			super.setValue(true);
		}
		if(name.equals("false")){
			super.setValue(false);
		}
	}
	
	@Override 
	public void printAssembly(PrintWriter pw, String prefix){
		pw.print(super.getValue());
	}


}
