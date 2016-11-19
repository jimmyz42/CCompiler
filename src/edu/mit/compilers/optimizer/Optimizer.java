package edu.mit.compilers.optimizer;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.List;

import edu.mit.compilers.cfg.Condition;
import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.highir.descriptor.Descriptor;
import edu.mit.compilers.highir.nodes.Expression;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.ImmediateValue;
import edu.mit.compilers.lowir.Memory;
import edu.mit.compilers.lowir.Register;
import edu.mit.compilers.lowir.Storage;
import edu.mit.compilers.lowir.instructions.Cmp;
import edu.mit.compilers.lowir.instructions.Je;
import edu.mit.compilers.lowir.instructions.Jmp;
import edu.mit.compilers.lowir.instructions.Label;
import edu.mit.compilers.lowir.instructions.Mov;
import edu.mit.compilers.lowir.instructions.Syscall;

public class Optimizer {
	private List<BasicBlock> orderedBlocks;
	private OptimizerContext ctx;

	public Optimizer(OptimizerContext ctx, List<BasicBlock> orderedBlocks) {
		this.orderedBlocks = orderedBlocks;
		this.ctx = ctx;
	}

	// generateTemporaries and doCSE combined make expressions linear
	public void run() {
		generateTemporaries();
		doCSE();
		doAlgebraicSimplification();
		doDeadCodeEliminiation();
	}

	public void doAlgebraicSimplification() {
		for(BasicBlock block: orderedBlocks) {
			block.doAlgebraicSimplification();
		}
	}

	public void doDeadCodeEliminiation() {
		HashSet<Descriptor> consumed = new HashSet<>();
		for(int blockNum = orderedBlocks.size() -1; blockNum >= 0; blockNum--) {
			BasicBlock currentBlock = orderedBlocks.get(blockNum);
			if(currentBlock.getPreviousBlock() != null) {
				Condition branchCondition = currentBlock.getPreviousBlock().getCondition();
				if(branchCondition != null)
					consumed.addAll(branchCondition.getConsumedDescriptors());
			}
			consumed = currentBlock.doDeadCodeEliminiation(consumed);
		}
	}

	public void doCSE() {
		for(int blockNum = orderedBlocks.size() -1; blockNum >= 0; blockNum--) {
			BasicBlock currentBlock = orderedBlocks.get(blockNum);

			currentBlock.doCSE(ctx);
		}
	}
	
	public void generateTemporaries() {
		for(BasicBlock block: orderedBlocks) {
			block.generateTemporaries(ctx);
		}		
	}

	public void cfgPrint(PrintWriter pw, String prefix) {
		for(int blockNum = 0; blockNum < orderedBlocks.size(); blockNum++) {
			BasicBlock currentBlock = orderedBlocks.get(blockNum);

			pw.println(prefix + "BasicBlock " + currentBlock.getID() + ":");
			currentBlock.cfgPrint(pw, prefix + "    ");
			pw.println(prefix + currentBlock.getID() + " points to:");
			for(BasicBlock block : currentBlock.getNextBlocks()){
				pw.println(prefix + "    " + block.getID());
			}
		}
	}

	public void generateAssembly(AssemblyContext ctx) {
		for(int blockNum = 0; blockNum < orderedBlocks.size()-1; blockNum++) {
			BasicBlock currentBlock = orderedBlocks.get(blockNum);
			ctx.addInstruction(Label.create(currentBlock.getID()));
			currentBlock.generateAssembly(ctx);

			//get conditional value generated at the end of currentBlock
			if(currentBlock.getCondition() != null) {
				Register val = ((Expression) currentBlock.getCondition()).allocateRegister(ctx);
				//compare to 1
				Storage temp = Register.create("%rax");
				Storage btrue = ImmediateValue.create(true);
				ctx.addInstruction(Mov.create(btrue, temp));
				ctx.addInstruction(new Cmp(val, temp));
				ctx.deallocateRegister(val);
				//if its == 1, go down true branch
				ctx.addInstruction(Je.create(Memory.create(currentBlock.getNextBlock(true).getID())));
				//else, go down TRUE branch
				ctx.addInstruction(Jmp.create(Memory.create(currentBlock.getNextBlock(false).getID())));
			} else {
				ctx.addInstruction(Jmp.create(Memory.create(currentBlock.getNextBlock(true).getID())));
			}
		}
		BasicBlock currentBlock = orderedBlocks.get(orderedBlocks.size()-1);
		ctx.addInstruction(Label.create(currentBlock.getID()));
		currentBlock.generateAssembly(ctx);

		// Array Index Out Of Bounds Handler
		ctx.addInstruction(Label.create("array_index_error"));
		ctx.addInstruction(Mov.create(ImmediateValue.create(60), Register.create("%rax")));
		ctx.addInstruction(Mov.create(ImmediateValue.create(-1), Register.create("%rdi")));
		ctx.addInstruction(Syscall.create());

		// Fall Off Method Handler
		ctx.addInstruction(Label.create("fall_off_error"));
		ctx.addInstruction(Mov.create(ImmediateValue.create(60), Register.create("%rax")));
		ctx.addInstruction(Mov.create(ImmediateValue.create(-2), Register.create("%rdi")));
		ctx.addInstruction(Syscall.create());
	}

	@Override
	public String toString() {
		StringWriter sw = new StringWriter();
		cfgPrint(new PrintWriter(sw), "");
		return sw.toString();
	}

	public static Optimizer create(OptimizerContext ctx, List<BasicBlock> orderedBlocks) {
		Optimizer optimizer = new Optimizer(ctx, orderedBlocks);
		return optimizer;
	}
}
