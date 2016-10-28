// Generated from /home/firescar/Documents/MIT/6.035/nchinda26035/src/edu/mit/compilers/grammar/Decaf.g4 by ANTLR 4.5.3

package edu.mit.compilers.lowir;

import java.util.Stack;
import java.util.ArrayList;
import java.util.HashMap;

import edu.mit.compilers.highir.nodes.Ir;
import edu.mit.compilers.lowir.Register;

/**
 * This class walks through a high level DecafSemanticChecker graph a low level
 * control flow graph which can be used to output assembly code
 */
public class AssemblyContext {
	private ArrayList<Object> stack = new ArrayList<>();
	private HashMap<Ir, Integer> stackLocations = new HashMap<>();
	private Stack<Register> registers = new Stack<>();
	private HashMap<Ir, Register> registerLocations = new HashMap<>();

	public AssemblyContext() {
		registers.push(Register.create("%rax"));
		registers.push(Register.create("%rbx"));
		registers.push(Register.create("%rcx"));
		registers.push(Register.create("%rdx"));
		registers.push(Register.create("%rsp"));
		registers.push(Register.create("%rbp"));
		registers.push(Register.create("%rsi"));
		registers.push(Register.create("%rdi"));
		for (int i = 8; i <= 15; i++) {
			registers.push(Register.create("%r" + i));
		}
	}

	public Object popStack() {
		stackLocations.values().remove(stack.size() - 1);
		return stack.remove(stack.size() - 1);
	}

	public void pushStack(Ir node, Storage loc) {
		if (stackLocations.containsKey(node)) {
			stack.set(stackLocations.get(node), loc);
		} else {
			stack.add(loc);
			stackLocations.put(node, stack.size() - 1);
		}
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
		reg.setValue(stack.get(stackLocations.get(node)));
		return reg;
	}

	// release a register back into the pool
	public void deallocateRegister(Ir node) {
		Register reg = registerLocations.remove(node);
		registers.push(reg);
		stack.set(stackLocations.get(node), reg.getValue());
	}
}