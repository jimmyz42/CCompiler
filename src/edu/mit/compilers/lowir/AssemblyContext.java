// Generated from /home/firescar/Documents/MIT/6.035/nchinda26035/src/edu/mit/compilers/grammar/Decaf.g4 by ANTLR 4.5.3

package edu.mit.compilers.lowir;

import java.util.Stack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.mit.compilers.highir.nodes.Expression;
import edu.mit.compilers.highir.nodes.Ir;
import edu.mit.compilers.lowir.Register;
import edu.mit.compilers.lowir.instructions.Add;
import edu.mit.compilers.lowir.instructions.Enter;
import edu.mit.compilers.lowir.instructions.Instruction;
import edu.mit.compilers.lowir.instructions.Leave;
import edu.mit.compilers.lowir.instructions.Mov;
import edu.mit.compilers.lowir.instructions.Pop;
import edu.mit.compilers.lowir.instructions.Push;
import edu.mit.compilers.lowir.instructions.Ret;
import edu.mit.compilers.lowir.instructions.Sub;

/**
 * This class walks through a high level DecafSemanticChecker graph a low level
 * control flow graph which can be used to output assembly code
 */
public class AssemblyContext {
	private HashMap<Storable, Integer> stackPositions = new HashMap<>();
	private Stack<Register> registers = new Stack<>();

	private Stack<HashMap<Storable, Integer>> stackPositionsStack = new Stack<>();
	private Stack<Stack<Register>> registersStack = new Stack<>();

	private List<Instruction> instructions = new ArrayList<>();
	private List<Instruction> footerInstructions = new ArrayList<>();

	public AssemblyContext() {
		for (int i = 10; i <= 15; i++) {
			registers.push(Register.create("%r" + i));
		}
	}

	public void setStackPosition(StorageTuple node, int position) {
		stackPositions.put(node.base, position);
	}

	public void allocateStack(StorageTuple node) {
		if (!stackPositions.containsKey(node)) {
			stackPositions.put(node.base, stackPositions.size() + 1);
		}
	}

	public void storeStack(StorageTuple node, Storage loc) {
		if (stackPositions.containsKey(node.base)) {
			Storage stackLocation = getStackLocation(node);
			addInstruction(Mov.create(loc, stackLocation));
		} else {
			stackPositions.put(node.base, stackPositions.size() + 1);
			addInstruction(Mov.create(loc, getStackLocation(node)));
		}
	}

	// return a register for use
	public Register allocateRegister() {
		Register reg = registers.pop();
		return reg;
	}
	public Register allocateRegister(StorageTuple node) {
		return allocateRegister(node, false);
	}
	public Register allocateRegister(StorageTuple node, boolean hasIndex) {
		if (!stackPositions.containsKey(node.base)) {
			storeStack(node, ImmediateValue.create(0));
		}

		Register reg = registers.pop();
		Storage stackLocation = getStackLocation(node, hasIndex);
		addInstruction(Mov.create(stackLocation, reg));
		return reg;
	}

	public Memory getStackLocation(StorageTuple node) {
		return getStackLocation(node, false);
	}
	public Memory getStackLocation(StorageTuple node, boolean hasIndex) {
		if(hasIndex) {
			String name = (-8*(stackPositions.get(node.base))) + "(%rbp, " + node.index.getName() + ", 8)";
			return Memory.create(name);
		}
		String name = (-8*(stackPositions.get(node.base))) + "(%rbp)";
		return Memory.create(name);
	}

	// release a register back into the pool
	public void deallocateRegister(Register reg) {
		if (reg == null) {
			return;
		}
		registers.push(reg);
	}

	public void enter(long numStackAllocations) {
		stackPositionsStack.push(stackPositions);
		stackPositions = new HashMap<>();
		addInstruction(Push.create(Register.create("%rbp")));
		addInstruction(Mov.create(Register.create("%rsp"),Register.create("%rbp")));

		registersStack.push(registers);
		registers = new Stack<>();
		for (int i = 10; i <= 15; i++) {
			registers.push(Register.create("%r" + i));
			//			addInstruction(Push.create(Register.create("%r" + i)));
		}

		//add enough space to the stack for the pushed registers and the future stack allocations
		addInstruction(Sub.create(ImmediateValue.create((numStackAllocations)*8), Register.create("%rsp")));
	}

	public void leave(long numStackAllocations) {
		registers = registersStack.pop();

		//remove the space the stack for the pushed registers and stack allocations
		addInstruction(Add.create(ImmediateValue.create((numStackAllocations)*8), Register.create("%rsp")));
		//		for (int i = 15; i >= 10; i--) {
		//			addInstruction(Pop.create(Register.create("%r" + i)));
		//		}
		addInstruction(Pop.create(Register.create("%rbp")));
		addInstruction(Ret.create());

		stackPositions = stackPositionsStack.pop();

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

	public int getStackSize(){
		return stackPositions.size();
	}
}
