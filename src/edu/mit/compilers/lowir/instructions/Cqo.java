package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

public class Cqo extends Instruction {

	public static Cqo create() {
		return new Cqo();
	}
	
	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
        pw.println(prefix + "cqo");
	}
}
