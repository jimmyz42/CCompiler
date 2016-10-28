// Generated from /home/firescar/Documents/MIT/6.035/nchinda26035/src/edu/mit/compilers/grammar/Decaf.g4 by ANTLR 4.5.3

package edu.mit.compilers.lowir;

import java.util.Stack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.mit.compilers.highir.nodes.Ir;
import edu.mit.compilers.lowir.Register;
import edu.mit.compilers.lowir.instructions.Instruction;
import edu.mit.compilers.lowir.instructions.Pop;
import edu.mit.compilers.lowir.instructions.Push;

/**
 * This class walks through a high level DecafSemanticChecker graph a low level
 * control flow graph which can be used to output assembly code
 */
public class AssemblyContext {
	private ArrayList<Object> stack = new ArrayList<>();
	private HashMap<Ir, Integer> stackLocations = new HashMap<>();
	private Stack<Register> registers = new Stack<>();
	private HashMap<Ir, Register> registerLocations = new HashMap<>();
	private List<Instruction> instructions = new ArrayList<>();

	public AssemblyContext() {
//		registers.push(Register.create("%rax"));
//		registers.push(Register.create("%rbx"));
//		registers.push(Register.create("%rcx"));
//		registers.push(Register.create("%rdx"));
//		registers.push(Register.create("%rsp"));
//		registers.push(Register.create("%rbp"));
//		registers.push(Register.create("%rsi"));
//		registers.push(Register.create("%rdi"));
		for (int i = 8; i <= 15; i++) {
			registers.push(Register.create("%r" + i));
		}
	}

	public void popStack(Storage loc) {
		stackLocations.values().remove(stack.size() - 1);
		Object value = stack.remove(stack.size() - 1);
		loc.setValue(value);
		addInstruction(Pop.create(loc));
	}

	public void pushStack(Ir node, Storage loc) {
		if (stackLocations.containsKey(node)) {
			stack.set(stackLocations.get(node), loc);
		} else {
			stack.add(loc);
			stackLocations.put(node, stack.size() - 1);
		}
		addInstruction(Push.create(loc));
	}

	// return a register for use
	public Register allocateRegister(Ir node) {
		if (!stackLocations.containsKey(node)) {
			pushStack(node, ImmediateValue.create(0));
		}
		if (registerLocations.get(node) != null) {
			return registerLocations.get(node);
		}

		Register reg = registers.pop();
		registerLocations.put(node, reg);
		//Memory stackPosition = getStackPosition(node); //TODO: get the text position in relation to the break pointer
		//and then create a mov instruction from stack to register
		reg.setValue(stack.get(stackLocations.get(node)));
		return reg;
	}

	// release a register back into the pool
	public void deallocateRegister(Ir node) {
		Register reg = registerLocations.remove(node);
		registers.push(reg);
		stack.set(stackLocations.get(node), reg.getValue());
	}

	public void addInstructions(List<Instruction> instructions) {
		this.instructions.addAll(instructions);
	}

	public void addInstruction(Instruction instruction) {
		this.instructions.add(instruction);
	}

	public List<Instruction> getInstructions() {
		return instructions;
	}
}