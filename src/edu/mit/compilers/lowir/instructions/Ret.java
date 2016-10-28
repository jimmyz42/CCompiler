package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

public class Ret extends Instruction {
    public Ret() {
    }

    public static Ret create() {
        return new Ret();
    }

	@Override
	public void print(PrintWriter pw, String prefix) {
		// TODO Auto-generated method stub
		
	}
}