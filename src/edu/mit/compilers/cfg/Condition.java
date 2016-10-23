package edu.mit.compilers.cfg;

import java.util.List;

import edu.mit.compilers.lowir.instructions.Instruction;

public interface Condition {
	// Represents a boolean condition for jumping
	// TODO make compare operators <, >, <=, >=, ==, != implement this
	// TODO generate corresponding jump statements
	// BoolLiteral also implements this TODO generate unconditional jump
	
	public List<Instruction> generateAssembly();
}
