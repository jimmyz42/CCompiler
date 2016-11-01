// Generated from /home/firescar/Documents/MIT/6.035/nchinda26035/src/edu/mit/compilers/grammar/Decaf.g4 by ANTLR 4.5.3

package edu.mit.compilers.lowir;

import java.util.Stack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.mit.compilers.highir.nodes.Expression;
import edu.mit.compilers.highir.nodes.Ir;
import edu.mit.compilers.lowir.Register;
import edu.mit.compilers.lowir.instructions.Enter;
import edu.mit.compilers.lowir.instructions.Instruction;
import edu.mit.compilers.lowir.instructions.Leave;
import edu.mit.compilers.lowir.instructions.Mov;
import edu.mit.compilers.lowir.instructions.Pop;
import edu.mit.compilers.lowir.instructions.Push;
import edu.mit.compilers.lowir.instructions.Ret;

/**
 * This class walks through a high level DecafSemanticChecker graph a low level
 * control flow graph which can be used to output assembly code
 */
public class AssemblyContext {
	private HashMap<Storable, Integer> stackPositions = new HashMap<>();
	private Stack<Register> registers = new Stack<>();
	private HashMap<Storable, Register> registerLocations = new HashMap<>();

	private Stack<HashMap<Storable, Integer>> stackPositionsStack = new Stack<>();
	private Stack<Stack<Register>> registersStack = new Stack<>();

	private List<Instruction> instructions = new ArrayList<>();
	private List<Instruction> footerInstructions = new ArrayList<>();

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

	public void setStackPosition(Storable node, int position) {
		stackPositions.put(node, position);
	}

	public void storeStack(Storable node, Storage loc) {
		if (stackPositions.containsKey(node)) {
			Storage stackLocation = node.getLocation(this);
			addInstruction(Mov.create(loc, stackLocation));
		} else {
			stackPositions.put(node, stackPositions.size() + 1);
			addInstruction(Mov.create(loc, getStackLocation(node)));
		}
	}

	// return a register for use
	public Register allocateRegister(Storable node) {
		if (!stackPositions.containsKey(node)) {
			storeStack(node, ImmediateValue.create(0));
		}
		if (registerLocations.get(node) != null) {
			return registerLocations.get(node);
		}

		Register reg = registers.pop();
		Storage stackLocation = node.getLocation(this);
		addInstruction(Mov.create(stackLocation, reg));
		registerLocations.put(node, reg);
		return reg;
	}

	public Memory getStackLocation(Storable node) {
		String name = (-8*(stackPositions.get(node))) + "(%rbp)";
		return Memory.create(name);
	}

	// release a register back into the pool
	public void deallocateRegister(Storable node) {
		Register reg = registerLocations.remove(node);
		if (reg == null) {
			return;
		}
		Memory stackLocation = getStackLocation(node);
		addInstruction(Mov.create(reg, stackLocation));
		registers.push(reg);
	}

	public void enter(int numStackAllocations) {
		stackPositionsStack.push(stackPositions);
		stackPositions = new HashMap<>();
		addInstruction(Enter.create(numStackAllocations + 6)); //6 for the number of registers

		registersStack.push(registers);
		registers = new Stack<>();
		for (int i = 10; i <= 15; i++) {
			registers.push(Register.create("%r" + i));
			addInstruction(Push.create(Register.create("%r" + i)));
		}
	}

	public void leave(boolean isMethodEnd) {
		if(isMethodEnd) {
			registers = registersStack.pop();

			for (int i = 15; i >= 10; i--) {
				addInstruction(Pop.create(Register.create("%r" + i)));
			}
	        addInstruction(Leave.create());
	        addInstruction(Ret.create());
	        
			stackPositions = stackPositionsStack.pop();
		} else {
			for (int i = 15; i >= 10; i--) {
				addInstruction(Pop.create(Register.create("%r" + i)));
			}
	        addInstruction(Leave.create());
	        addInstruction(Ret.create());
		}
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
