package edu.mit.compilers.optimizer;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.BitSet;
import java.util.Set;

import edu.mit.compilers.cfg.Condition;
import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.cfg.components.EnterBlock;
import edu.mit.compilers.cfg.components.LeaveBlock;
import edu.mit.compilers.highir.descriptor.Descriptor;
import edu.mit.compilers.highir.descriptor.VariableDescriptor;
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
		//TODO: keep looping through these until modifications are no longer being made by keeping track of whether
		//modifications have been made in Optimizer Context
		//using a constant like this is a dirty Hack
		for(int i = 0; i < 1; i++) {
			doConstantFolding();
			doAlgebraicSimplification();
			// generateTemporaries();
			// doCSE();
			// for(int j = 0; j < 5; j++) {
			// 	doCopyPropagation();
			// }
			// doConstantPropagation();
			doReachingDefinitions();
			//doUnreachableCodeElimination();
			//doDeadCodeEliminiation();
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
		for(BasicBlock block: orderedBlocks) {
			block.generateTemporaries(ctx);
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

	//NOTE: different than in lecture because using maps instead of bit vectors
	public void doReachingDefinitions(){
		System.out.println("DOING REACHING DEFS");

		//list of methods; contain list of basic blocks in methods
		List<List<BasicBlock>> methods = new ArrayList<>(); 
		int methodNum = 0;
		boolean isGlobal = true; //used to skip over all the global bbs

		//separate the basic blocks into their methods
		for(BasicBlock block : orderedBlocks){

			if(block instanceof EnterBlock){
				//new method
				methods.add(new ArrayList<BasicBlock>());
				isGlobal = false;
			} else if (block instanceof LeaveBlock){
				//end of method
				methodNum++;
			} else if(!isGlobal){
				methods.get(methodNum).add(block);
			}
		}
		

		//for each method, instantiate bit vecotrs 
		for(List<BasicBlock> method : methods){
			System.out.println("//////////////// NEW METHOD ////////////");
			//clear everything in ctx that needs to be cleared 

			//number definitions
			ctx.resetAssignStmtCount();
			ctx.getAssignStmtToInt().clear();
			for(BasicBlock block : method){
				block.numberDefinitions(ctx);
			}

			//create map of variables to definitions
			ctx.getVarToDefs().clear();
			for(BasicBlock block : method){
				block.findVarToDefs(ctx);
			}

			System.out.println("AssignStmtToInt-----------");
			System.out.println(ctx.prettyPrintAssignStmtToInt());

			System.out.println("VarToDefs-----------------");
			System.out.println(ctx.prettyPrintVarToDefs());

			//for each basic block, instantiate gen and kill sets
			for(BasicBlock block : method){
				block.makeGenSet(ctx);
				block.makeKillSet(ctx);
			}

			//calculate in and out sets 
			for(BasicBlock block : method){
				ctx.getRdOut().put(block, new BitSet(ctx.getAssignStmtCount()));
			}
			BasicBlock entryBlock = method.get(0);
			ctx.getRdIn().put(entryBlock, new BitSet(ctx.getAssignStmtCount()));
			ctx.getRdOut().put(entryBlock, ctx.getRdGen().get(entryBlock));

			Set<BasicBlock> changed = new HashSet<>(method);
			changed.remove(entryBlock);

			while(!changed.isEmpty()){
				BasicBlock n = changed.iterator().next();
				changed.remove(n);
				
				ctx.getRdIn().put(n, new BitSet(ctx.getAssignStmtCount())); //IN[n] = emptyset

				for (BasicBlock p : n.getPreviousBlocks()){
					BitSet in_n = ctx.getRdIn().get(n);
					BitSet out_p = ctx.getRdOut().get(p);
					in_n.or(out_p);
					ctx.getRdIn().put(n, in_n);
				}

				BitSet in = ctx.getRdIn().get(n);
				BitSet kill = ctx.getRdKill().get(n);
				BitSet gen = ctx.getRdGen().get(n);
				in.xor(kill);
				gen.or(in);
				BitSet new_out = gen;

				BitSet old_out = ctx.getRdOut().get(n);
				ctx.getRdOut().put(n, new_out);

				if (!old_out.equals(new_out)){
					for(BasicBlock s : n.getNextBlocks()){
						changed.add(s);
					}
				}
			}

		}

		// for(BasicBlock block: orderedBlocks) {
		// 	block.getConstOut().clear();
		// }
		// //TODO: verify that first block in orderedBlocks is the entry block
		// BasicBlock entryBlock = orderedBlocks.get(0);
		// entryBlock.getConstIn().clear();
		// entryBlock.getConstOut() = entryBlock.getConstGen();
		// Set<BasicBlock> changed = new HashSet<>(orderedBlocks);
		// changed.remove(entryBlock);

		// while(!changed.isEmpty()){
		// 	BasicBlock currentBlock;
		// 	//choose a node n in changed
		// 	for(BasicBlock block : changed){
		// 		currentBlock = block;
		// 		break;
		// 	}
		// 	changed.remove(currentBlock);
		// 	Map<> in = currentBlock.getConstIn();
		// 	in.clear(); //emptyset
		// 	//for all predecessors 
		// 	for(BasicBlock block : currentBlock.getPreviousBlocks()){
		// 		// in = in union block.out 
		// 	}
		// 	Map<> out = currentBlock.getConstOut();
		// 	// out = out union (in - kill)

		// 	//if out changed 
		// 	for(BasicBlock block : currentBlock.getNextBlocks()){
		// 		changed.add(block);
		// 	}


		// }
	}

	public void doConstantPropagation(){
		for(BasicBlock block: orderedBlocks) {
			block.doConstantPropagation(ctx);
		}
	}

	public void doCSE() {
		for(int blockNum = orderedBlocks.size() -1; blockNum >= 0; blockNum--) {
			BasicBlock currentBlock = orderedBlocks.get(blockNum);

			currentBlock.doCSE(ctx);
		}
	}

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
