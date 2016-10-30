// Generated from /home/firescar/Documents/MIT/6.035/nchinda26035/src/edu/mit/compilers/grammar/Decaf.g4 by ANTLR 4.5.3

package edu.mit.compilers.lowir;

import java.util.Stack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.mit.compilers.cfg.CFGAble;
import edu.mit.compilers.highir.nodes.Ir;
import edu.mit.compilers.lowir.Register;
import edu.mit.compilers.lowir.instructions.Enter;
import edu.mit.compilers.lowir.instructions.Instruction;
import edu.mit.compilers.lowir.instructions.Leave;
import edu.mit.compilers.lowir.instructions.Mov;
import edu.mit.compilers.lowir.instructions.Pop;
import edu.mit.compilers.lowir.instructions.Push;

/**
 * This class walks through a high level DecafSemanticChecker graph a low level
 * control flow graph which can be used to output assembly code
 */
public class AssemblyContext {
	private ArrayList<Object> stack = new ArrayList<>();
	private HashMap<CFGAble, Integer> stackPositions = new HashMap<>();
	private Stack<Register> registers = new Stack<>();
	private HashMap<CFGAble, Register> registerLocations = new HashMap<>();

	private Stack<Integer> breakPointerStack = new Stack<>();
	private Stack<Stack<Register>> registersStack = new Stack<>();

	private List<Instruction> instructions = new ArrayList<>();
	private List<Instruction> footerInstructions = new ArrayList<>();

	private int breakPointer;

	public AssemblyContext() {
		//		registers.push(Register.create("%rax"));
		//		registers.push(Register.create("%rbx"));
		//		registers.push(Register.create("%rcx"));
		//		registers.push(Register.create("%rdx"));
		//		registers.push(Register.create("%rsp"));
		//		registers.push(Register.create("%rbp"));
		//		registers.push(Register.create("%rsi"));
		//		registers.push(Register.create("%rdi"));
		//		registers.push(Register.create("%r8"));
		//		registers.push(Register.create("%r9"));
		for (int i = 10; i <= 15; i++) {
			registers.push(Register.create("%r" + i));
		}
	}

	public void setStackPosition(CFGAble node, int position) {
		stackPositions.put(node, breakPointer - position);
	}

	public void popStack(Storage loc) {
		stackPositions.values().remove(stack.size() - 1);
		Object value = stack.remove(stack.size() - 1);
		loc.setValue(value);
		addInstruction(Pop.create(loc));
	}

	public void pushStack(CFGAble node, Storage loc) {
		if (stackPositions.containsKey(node)) {
			stack.set(stackPositions.get(node), loc.getValue());
    		Memory stackLocation = getStackLocation(node);
    		addInstruction(Mov.create(loc, stackLocation));
		} else {
			stack.add(loc);
			stackPositions.put(node, stack.size() - 1);
    		addInstruction(Push.create(loc));
		}
	}

	// return a register for use
	public Register allocateRegister(CFGAble node) {
		if (!stackPositions.containsKey(node)) {
			pushStack(node, ImmediateValue.create(0));
		}
		if (registerLocations.get(node) != null) {
			return registerLocations.get(node);
		}

		Register reg = registers.pop();
		Memory stackLocation = getStackLocation(node);
		addInstruction(Mov.create(stackLocation, reg));
		registerLocations.put(node, reg);
		reg.setValue(stack.get(stackPositions.get(node)));
		return reg;
	}

	public Memory getStackLocation(CFGAble node) {
		String name = (-8*(stackPositions.get(node)+1 - breakPointer)) + "(%rbp)";
		return Memory.create(name);
	}

	// release a register back into the pool
	public void deallocateRegister(CFGAble node) {
		Register reg = registerLocations.remove(node);
		Memory stackLocation = getStackLocation(node);
		addInstruction(Mov.create(reg, stackLocation));
		registers.push(reg);
		stack.set(stackPositions.get(node), reg.getValue());
	}

	public void enter() {
        breakPointerStack.push(breakPointer);
        breakPointer = stackPositions.size();
        addInstruction(Enter.create(0));

        registersStack.push(registers);
		for (int i = 10; i <= 15; i++) {
    		addInstruction(Push.create(Register.create("%r" + i)));
		}
	}

	public void leave() {
		//TODO this code might break when dealing with multiple return statements in a function
		//in the case we shouldn't pop till all paths are taken
        registers = registersStack.pop();
		for (int i = 15; i >= 10; i--) {
    		addInstruction(Pop.create(Register.create("%r" + i)));
		}

        breakPointer = breakPointerStack.pop();
        addInstruction(Leave.create());
	}

	public void addInstructions(List<Instruction> instructions) {
		this.instructions.addAll(instructions);
	}

	public void addInstruction(Instruction instruction) {
		this.instructions.add(instruction);
	}

	public void addFooterInstruction(Instruction instruction) {
		this.footerInstructions.add(instruction);
	}

	public List<Instruction> getInstructions() {
		List<Instruction> completeList = new ArrayList<>();
		completeList.addAll(instructions);
		completeList.addAll(footerInstructions);
		return completeList;
	}
}
