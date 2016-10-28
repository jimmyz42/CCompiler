package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

public class Ret extends Instruction {
    public Ret() {
    }

    public static Ret create() {
        return new Ret();
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
		pw.println(prefix + "ret");		
	}
}