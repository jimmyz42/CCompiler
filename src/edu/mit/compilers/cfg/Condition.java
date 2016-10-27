package edu.mit.compilers.cfg;

import java.io.PrintWriter;
import java.util.List;

import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.instructions.Instruction;

public interface Condition {
	// Represents a boolean condition for jumping
	// TODO generate corresponding jump statements
	// BoolLiteral also implements this TODO generate unconditional jump
	
	public List<Instruction> generateAssembly(AssemblyContext ctx);
	public void cfgPrint(PrintWriter pw, String prefix);
}
