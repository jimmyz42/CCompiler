package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

public class Syscall extends Instruction {
    public Syscall() {
    }
    
    public static Syscall create() {
    	return new Syscall();
    }

	@Override
	public void printAssembly(PrintWriter pw, String prefix) {
        pw.println(prefix + "syscall");
	}
}