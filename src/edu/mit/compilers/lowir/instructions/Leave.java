package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

public class Leave extends Instruction {
    public Leave() {
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
		pw.println(prefix + "leave");		
	}
}