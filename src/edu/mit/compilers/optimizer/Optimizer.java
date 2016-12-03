package edu.mit.compilers.optimizer;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import edu.mit.compilers.cfg.Condition;
import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.cfg.components.LeaveBlock;
import edu.mit.compilers.highir.descriptor.Descriptor;
import edu.mit.compilers.highir.nodes.Expression;
import edu.mit.compilers.highir.nodes.Location;
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
	
	// For Global CSE
	private List<Set<Expression>> cseGenExprs;
	private List<Set<Location>> cseKillVars;
	private List<Set<Expression>> availableExpressions;
	
	public Optimizer(OptimizerContext ctx, List<BasicBlock> orderedBlocks) {
		this.orderedBlocks = orderedBlocks;
		this.ctx = ctx;
	}

	// generateTemporaries and doCSE combined make expressions linear
	public void run() {
		//TODO: keep looping through these until modifications are no longer being made by keeping track of whether
		//modifications have been made in Optimizer Context
		//using a constant like this is a dirty Hack

		for(int i = 0; i < 1; i++) {
			// reset optimizer to clear set/maps from prev iteration
			ctx = new OptimizerContext();
			doConstantFolding();
			doAlgebraicSimplification();
			generateTemporaries();
			doGlobalCSE();
			doLocalCSE();
			//doCSE(); 
			for(int j = 0; j < 5; j++) {
				doCopyPropagation();
			}
			doConstantPropagation();
			doUnreachableCodeElimination();
			doDeadCodeEliminiation();
		}
	}

	public void doConstantFolding() {
		for(BasicBlock block: orderedBlocks) {
			block.doConstantFolding();
		}
	}

	public void doAlgebraicSimplification() {
		for(BasicBlock block: orderedBlocks) {
			block.doAlgebraicSimplification();
		}
	}

	public void generateTemporaries() {
		cseGenExprs = new ArrayList<>();
		cseKillVars = new ArrayList<>();
		
		for(BasicBlock block: orderedBlocks) {
			ctx.getCSEGenExprs().clear();
			ctx.getCSEKillVars().clear();
			block.generateTemporaries(ctx);
			cseGenExprs.add(new HashSet<Expression>(ctx.getCSEGenExprs()));
			cseKillVars.add(new HashSet<Location>(ctx.getCSEKillVars()));
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

	public void doCopyPropagation(){
		for(int blockNum = orderedBlocks.size() -1; blockNum >= 0; blockNum--) {
			BasicBlock currentBlock = orderedBlocks.get(blockNum);

			currentBlock.doCopyPropagation(ctx);
		}
	}

	public void doConstantPropagation(){
		for(BasicBlock block: orderedBlocks) {
			block.doConstantPropagation(ctx);
		}
	}
	
	public void doGlobalCSE() {
		List<BitSet> gen = new ArrayList<>();
		List<BitSet> kill = new ArrayList<>();
		List<BitSet> in = new ArrayList<>();
		List<BitSet> out = new ArrayList<>();
		HashMap<Expression, Integer> exprToVal = ctx.getExprToVal();
		HashMap<Integer, Expression> valToExpr = new HashMap<>();
		for(Expression expr: exprToVal.keySet()) {
			valToExpr.put(exprToVal.get(expr), expr);
		}
		int numExprs = ctx.getNumberOfTemps();
		
		// Generate GEN/KILL bitvectors
		for(int i=0; i<orderedBlocks.size(); i++) {
			BitSet curGen = new BitSet(numExprs);
			BitSet curKill = new BitSet(numExprs);
			
			// If Leave Block, needs to Kill All Expressions
			// since expressions cannot persist across methods
			if(orderedBlocks.get(i) instanceof LeaveBlock) {
				curKill.set(0, numExprs);
			} else {
				for(Location loc: cseKillVars.get(i)) {
					for(Expression expr: ctx.getExprsContainingVar(loc)) {
						curKill.set(exprToVal.get(expr));
					}
				}
				for(Expression expr: cseGenExprs.get(i)) {
					curKill.clear(exprToVal.get(expr));
					curGen.set(exprToVal.get(expr));
				}
			}
			gen.add(curGen);
			kill.add(curKill);
			
			BitSet curIn = new BitSet(numExprs);
			BitSet curOut = new BitSet(numExprs);
			curOut.set(0, numExprs);
			in.add(curIn);
			out.add(curOut);
		}
		
		// Fixed point algorithm
		in.get(0).clear();
		out.get(0).clear();
		out.get(0).or(gen.get(0));
		boolean change = true;
		while(change) {
			change = false;
			// Skip Block 0 because it's Entry Block
			for(int i=1; i<orderedBlocks.size(); i++) {
				in.get(i).set(0, numExprs);
				for(BasicBlock prev: orderedBlocks.get(i).getPreviousBlocks()) {
					in.get(i).and(out.get(prev.getNumID()));
				}
				BitSet origOut = (BitSet) out.get(i).clone();
				out.get(i).clear();
				out.get(i).or(in.get(i));
				out.get(i).andNot(kill.get(i));
				out.get(i).or(gen.get(i));
				if(!out.get(i).equals(origOut)) change = true;
			}
		}
		
		availableExpressions = new ArrayList<>();
		for(int block=0; block<orderedBlocks.size(); block++) {
			HashSet<Expression> curExprs = new HashSet<>();
			for(int val=0; val<numExprs; val++) {
				if(in.get(block).get(val)) {
					curExprs.add(valToExpr.get(val));
				}
			}
			availableExpressions.add(curExprs);
		}
	}
	
	public void doLocalCSE() {
		for(int blockNum=0; blockNum<orderedBlocks.size(); blockNum++) {
			ctx.getCSEAvailableExprs().clear();
			ctx.getCSEAvailableExprs().addAll(availableExpressions.get(blockNum));
			BasicBlock currentBlock = orderedBlocks.get(blockNum);
			currentBlock.doCSE(ctx);
		}
	}

//	public void doCSE() {
//		for(int blockNum = orderedBlocks.size() -1; blockNum >= 0; blockNum--) {
//			BasicBlock currentBlock = orderedBlocks.get(blockNum);
//
//			currentBlock.doCSE(ctx);
//		}
//	}

	public void doUnreachableCodeElimination() {
		HashSet<BasicBlock> reachable = new HashSet<BasicBlock>();
		List<BasicBlock> newBlocks = new ArrayList<BasicBlock>();
		
		if(orderedBlocks.size() == 0) {
			return;
		}
		orderedBlocks.get(0).doUnreachableCodeElimination();
		newBlocks.add(orderedBlocks.get(0));
		reachable.addAll(orderedBlocks.get(0).getNextBlocks());
		
		for(int blockNum = 1; blockNum < orderedBlocks.size(); blockNum++) {
			BasicBlock currentBlock = orderedBlocks.get(blockNum);
			if(!reachable.contains(currentBlock)) {
				continue;
			}
			currentBlock.doUnreachableCodeElimination();
			if(BasicBlock.canMerge(currentBlock, currentBlock.getNextBlock())) {
				currentBlock = BasicBlock.merge(currentBlock, currentBlock.getNextBlock());
			}
			reachable.addAll(currentBlock.getNextBlocks());
			newBlocks.add(currentBlock);
		}
		
		this.orderedBlocks = newBlocks;

		clearPrevBlocks();
		genPrevBlocks();
		giveAllBlocksIds();
	}

	private void giveAllBlocksIds(){
		for(int blockNum = 0; blockNum < orderedBlocks.size(); blockNum++) {
			BasicBlock currentBlock = orderedBlocks.get(blockNum);
			currentBlock.setID("block" +  blockNum);
		}
	}

	public void clearPrevBlocks(){
		HashSet<BasicBlock> visited = new HashSet<BasicBlock>();
		Queue<BasicBlock> blockQueue = new ArrayDeque<>();
		blockQueue.add(orderedBlocks.get(0));

		while(blockQueue.size() > 0) {
			BasicBlock currentBlock = blockQueue.poll();
			if(visited.contains(currentBlock)) continue;
			else visited.add(currentBlock);

			currentBlock.setPreviousBlocks(new ArrayList<BasicBlock>());
			if(currentBlock.getNextBlocks().size() > 0){
				blockQueue.add(currentBlock.getNextBlock(true));
			}
			if(currentBlock.getNextBlocks().size() > 1) {
				blockQueue.add(currentBlock.getNextBlock(false));
			}
		}
	}

	public void genPrevBlocks(){
		HashSet<BasicBlock> visited = new HashSet<BasicBlock>();
		Queue<BasicBlock> blockQueue = new ArrayDeque<>();
		blockQueue.add(orderedBlocks.get(0));

		while(blockQueue.size() > 0) {
			BasicBlock currentBlock = blockQueue.poll();
			if(visited.contains(currentBlock)) continue;
			else visited.add(currentBlock);

			if(currentBlock.getNextBlocks().size() > 0){
				currentBlock.getNextBlock(true).addPreviousBlock(currentBlock);
				blockQueue.add(currentBlock.getNextBlock(true));
			}
			if(currentBlock.getNextBlocks().size() > 1) {
				currentBlock.getNextBlock(false).addPreviousBlock(currentBlock);
				blockQueue.add(currentBlock.getNextBlock(false));
			}
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
