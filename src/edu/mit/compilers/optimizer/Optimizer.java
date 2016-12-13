package edu.mit.compilers.optimizer;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.LinkedList;

import edu.mit.compilers.cfg.Condition;
import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.cfg.components.EnterBlock;
import edu.mit.compilers.cfg.components.LeaveBlock;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.highir.descriptor.Descriptor;
import edu.mit.compilers.highir.descriptor.VariableDescriptor;
import edu.mit.compilers.highir.nodes.Expression;
import edu.mit.compilers.highir.nodes.Location;
import edu.mit.compilers.highir.nodes.AssignStmt;
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
	public static String[] optimizations = {"alg", "cse", "cp", "dce"};
	
	private Set<String> optsUsed;
	private List<BasicBlock> orderedBlocks;
	private OptimizerContext ctx;
	
	// For Global CSE
	private List<Set<Expression>> cseGenExprs;
	private List<Set<Location>> cseKillVars;
	private List<Set<Expression>> availableExpressions;

	// For loop optimizations
	//maps a bb to the set of bbs that dominate it
	private HashMap<BasicBlock, Set<BasicBlock>> dominationTree = new HashMap<>();
	
	public Optimizer(OptimizerContext ctx, List<BasicBlock> orderedBlocks) {
		this.optsUsed = new HashSet<>();
		this.orderedBlocks = orderedBlocks;
		this.ctx = ctx;
	}
	
	public void setOpts(boolean[] opts) {
		optsUsed.clear();
		for(int i=0; i<optimizations.length; i++) {
			if(opts[i]) optsUsed.add(optimizations[i]);
		}
	}

	// generateTemporaries and doCSE combined make expressions linear
	public void run() {
		//TODO: keep looping through these until modifications are no longer being made by keeping track of whether
		//modifications have been made in Optimizer Context
		//using a constant like this is a dirty Hack

		for(int i = 0; i < 1; i++) {
			// reset optimizer to clear set/maps from prev iteration
			ctx = new OptimizerContext();
			if(optsUsed.contains("alg")) {
				doAlgebraicSimplification(); // includes constant folding
			}
			generateTemporaries();
			doReachingDefinitions(); // is this for loop invariant code? yes - and globalConstProp
			doGlobalConstantPropagation();
			doLoopInvariantMotion();
			//doConstantPropagation();

			if(optsUsed.contains("cse")) {
				//generateTemporaries();
				//doGlobalCSE();
				//doLocalCSE();
			} 
			if(optsUsed.contains("cp")) {
				System.out.println("OptsUse cp=1");
				for(int j = 0; j < 5; j++) {
					//doCopyPropagation();
				}
				doConstantPropagation();
			}
			//doLiveness();
			if(optsUsed.contains("dce")) {
				//doUnreachableCodeElimination();
				//doDeadCodeEliminiation();
			}
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

	public void doLoopInvariantMotion(){
		//System.out.println("doing loop invariant motion");
		makeDominationTree();
		List<List<BasicBlock>> methods = getMethods(orderedBlocks);

		// System.out.println("Old ordered blocks ----");
		// for(BasicBlock b : orderedBlocks){
		// 	System.out.println(b);
		// }

		for(List<BasicBlock> method : methods){
			//find back edges 
			List<Tuple<BasicBlock, BasicBlock>> backedges = findBackEdges(method);
			//for each back edge, find set of blocks in loop
			//aka - for each loop, do loop invariant motion
			for(Tuple<BasicBlock, BasicBlock> edge : backedges){
				Set<BasicBlock> loop = findLoop(method, edge.x, edge.y);
				
				// System.out.println("Dealing w loop consising of: ");
				// System.out.println(loop);

				//set/clear necessary ctx things
				ctx.setCurrentLoop(loop);
				ctx.getInvariantStmts().clear();
				
				for(BasicBlock block : loop){
					//set ctx current block
					ctx.setCurrentBlock(block);

					block.detectLoopInvariantCode(ctx);
				}

				//System.out.println("Invariant Code: " + ctx.getInvariantStmts());

				//create pre-header 
				//note: edge.y is the header
				if(!ctx.getInvariantStmts().isEmpty()){
					removeInvariantCode(loop);
					createPreheader(edge.y);
				}
				

				// System.out.println("New ordered blocks ----");
				// for(BasicBlock b : orderedBlocks){
				// 	System.out.println(b);
				// }
			}
		}
	}

	public void removeInvariantCode(Set<BasicBlock> loop){
		for(BasicBlock block : loop){
			block.removeInvariantCode(ctx);
		}
	}

	public void createPreheader(BasicBlock header){
		//invariant code is in ctx.getInvariantStmts
		BasicBlock preheader = new BasicBlock(new ArrayList<Optimizable>(ctx.getInvariantStmts()));

		//for all preds for header, point them to preheader insead of header
		for(BasicBlock p : header.getPreviousBlocks()){
			//if p is in the loop, don't set it to point to preheader
			if(ctx.getCurrentLoop().contains(p)){
				continue;
			}
			p.resetNextBlocks(Arrays.asList(preheader)); //TODO: might not work
			preheader.addPreviousBlock(p);
		}

		preheader.resetNextBlocks(Arrays.asList(header));

		//remove pointer(s) from header to blocks that pred(preheader)
		List<BasicBlock> new_pred = new ArrayList<>();
		new_pred.add(preheader);

		//create set of pred(preheader)
		Set<BasicBlock> preheader_pred_set = new HashSet<>(preheader.getPreviousBlocks());

		for(BasicBlock pred : header.getPreviousBlocks()){
			if(!preheader_pred_set.contains(pred)){
				new_pred.add(pred);
			}
		}

		header.setPreviousBlocks(new_pred);

		//recreate CFG with optimizations, and orderedBlocks regenerated 
		BasicBlock startBlock = orderedBlocks.get(0);
		BasicBlock endBlock = orderedBlocks.get(orderedBlocks.size() -1);

		//note: preheader might get merged w/ block above it
		orderedBlocks = CFG.createWithOptimizations(startBlock, endBlock).getOrderedBlocks();

		// System.out.println("NEW CFG ---------------------------");
		// StringWriter sw = new StringWriter();
		// this.cfgPrint(new PrintWriter(sw), "");
		// System.out.println(sw.toString());
	}

	//returns list of backedges in method as tuples where x: n, y: d
	public List<Tuple<BasicBlock, BasicBlock>> findBackEdges(List<BasicBlock> method){
		//make DomTree must be done before this
		List<Tuple<BasicBlock, BasicBlock>> backEdges = new ArrayList<>();
		for (BasicBlock n : method){
			for(BasicBlock d: n.getNextBlocks()){
				//System.out.println("Does " + d + " dominate " + n + "? " + dominationTree.get(n).contains(d) + " : " + dominationTree.get(n));
				//does d dominate n?
				if(dominationTree.get(n).contains(d)){
					//yes! d is in n's dom set
					backEdges.add(new Tuple<BasicBlock, BasicBlock>(n, d));
				}
			}
		}
		return backEdges;
	}

	//returns set of bbs in the loop contained w/in backedge head ---->? tail
	public Set<BasicBlock> findLoop(List<BasicBlock> method, BasicBlock head, BasicBlock tail){
		Set<BasicBlock> loop = new HashSet<>();
		loop.add(tail);
		Set<BasicBlock> allBlocksInMethod = new HashSet<>(method);
		LinkedList<BasicBlock> stack = new LinkedList<>();
		insert(loop, stack, head);
		while(!stack.isEmpty()){
			BasicBlock m = stack.pop();
			for(BasicBlock p : m.getPreviousBlocks()){
				if(allBlocksInMethod.contains(p)){
					insert(loop, stack, p);
				}
			}
		}
		return loop;
	}

	private void insert(Set<BasicBlock> loop, LinkedList<BasicBlock> stack, BasicBlock m){
		if(!loop.contains(m)){
			loop.add(m);
			stack.push(m);
		}
	}

	public void makeDominationTree(){
		List<List<BasicBlock>> methods = getMethods(orderedBlocks);
		for(List<BasicBlock> method : methods){
			//System.out.println("NEW METHOD //////////////////");
			if(method.isEmpty()){
				continue;
			}
			BasicBlock entryBlock = method.get(0);
			dominationTree.put(entryBlock, new HashSet<>(Arrays.asList(entryBlock)));
			
			Set<BasicBlock> allBlocksInMethod = new HashSet<>(method);
			//method is now N - {n0}
			method.remove(0);

			for(BasicBlock block : method){
				dominationTree.put(block, new HashSet<>(method));
			}

			//while d changes
			boolean isChanging = true;
			while(isChanging){
				//System.out.println("isChanging = " + isChanging);
				for(BasicBlock block : method){
					
					Set<BasicBlock> domSet = new HashSet<>();
					BasicBlock previous = block.getPreviousBlock();

					if(block.getPreviousBlocks().size() == 1){
						if(allBlocksInMethod.contains(previous)){
							domSet.addAll(dominationTree.get(previous));
						}
					} else {
						if(allBlocksInMethod.contains(block.getPreviousBlocks().get(1))){
							domSet.addAll(dominationTree.get(block.getPreviousBlocks().get(1)));
						}
					}

					for(BasicBlock p : block.getPreviousBlocks()){
						if(allBlocksInMethod.contains(p)){
							domSet.retainAll(dominationTree.get(p)); 
						}
					}

					domSet.add(block);
					Set<BasicBlock> old_domSet = dominationTree.put(block, domSet);

					if(old_domSet.equals(domSet)){
						isChanging = false;
					}
				}
				if(method.size() <= 1){
					isChanging = false;
				}
			}
			//System.out.println(dominationTree);
		}
	}

	//takes orderedBlocks and splits it into lists by method
	//note: puts global blocks in list ctx.getGlobalBlocks()
	private List<List<BasicBlock>> getMethods(List<BasicBlock> orderedBlocks){
		List<List<BasicBlock>> methods = new ArrayList<>(); 
		int methodNum = 0;
		boolean isGlobal = true; //used to skip over all the global bbs

		//separate the basic blocks into their methods
		for(BasicBlock block : orderedBlocks){

			if(block instanceof EnterBlock){
				//new method
				methods.add(new ArrayList<BasicBlock>());
				isGlobal = false;
				//System.out.println("EnterBlock: " + block);
			} else if (block instanceof LeaveBlock){
				//end of method
				methodNum++;
				//System.out.println("LeaveBlock: " + block);
			} else if(!isGlobal){
				//System.out.println("adding " + block + " to method " + methodNum);
				methods.get(methodNum).add(block);
			} else {
				ctx.getGlobalBlocks().add(block);
			}
		}
		return methods;
	}

	public void doLiveness(){
		//list of methods; contain list of basic blocks in methods
		List<List<BasicBlock>> methods = getMethods(orderedBlocks);
		for(List<BasicBlock> method : methods){
			//System.out.println("//////////////// NEW METHOD ////////////");
			if(method.isEmpty()){
				continue;
			}
			//clear everything in ctx that needs to be cleared 
			ctx.resetVarCount();
			ctx.getLivVarToInt().clear();
			ctx.getLivIntToVar().clear();

			for(BasicBlock block : method){
				//number vars
				block.numberVariables(ctx);
			}

			//populating hashmaps for future use
			for(BasicBlock block : method){
				ctx.getBbLivVarToInt().put(block, ctx.getLivVarToInt());
				ctx.getBbLivIntToVar().put(block, ctx.getLivIntToVar());
			}

			// System.out.println("Var to Int Map ---------------");
			// System.out.println(ctx.getLivVarToInt());

			for(BasicBlock block : method){
				//create USE and DEF
				block.makeDefSet(ctx);
				block.makeUseSet(ctx);
			}

			// System.out.println("DEF --------------------");
			// System.out.println(ctx.getLivDef());

			// System.out.println("USE -------------------");
			// System.out.println(ctx.getLivUse());

			//calculate IN and OUT
			for(BasicBlock block : method){
				ctx.getLivIn().put(block, new BitSet(ctx.getVarCount()));
			}
			BasicBlock exit = method.get(method.size() - 1);
			ctx.getLivOut().put(exit, new BitSet(ctx.getVarCount()));
			ctx.getLivIn().put(exit, (BitSet)ctx.getLivUse().get(exit).clone());
			Set<BasicBlock> changed = new HashSet<>(method);
			Set<BasicBlock> allBlocksInMethod = new HashSet<>(method);
			changed.remove(exit);

			while(!changed.isEmpty()){
				BasicBlock n = changed.iterator().next();
				changed.remove(n);

				ctx.getLivOut().put(n, new BitSet(ctx.getVarCount()));

				for(BasicBlock s : n.getNextBlocks()){
					if(allBlocksInMethod.contains(s)){
						BitSet in_s = (BitSet)ctx.getLivIn().get(s).clone();
						BitSet out_n = (BitSet)ctx.getLivOut().get(n).clone();
						out_n.or(in_s);
						ctx.getLivOut().put(n, out_n);
					}
				}

				BitSet use_n = (BitSet)ctx.getLivUse().get(n).clone();
				BitSet out_n = (BitSet)ctx.getLivOut().get(n).clone();
				BitSet def_n = (BitSet)ctx.getLivDef().get(n).clone();
				out_n.andNot(def_n);
				use_n.or(out_n);
				BitSet new_in = use_n;
				BitSet old_in = ctx.getLivIn().put(n, new_in); //IN[n] = use[n] U (out[n] - def[n])

				if(!old_in.equals(new_in)){
					for(BasicBlock p : n.getPreviousBlocks()){
						if(allBlocksInMethod.contains(p)){
							changed.add(p);
						}
					}
				}
			}
			
			// System.out.println("IN -------------");
			// System.out.println(ctx.getLivIn());

			// System.out.println("OUT -------------");
			// System.out.println(ctx.getLivOut());

		}
	}

	public void reachingDefsHelper(List<BasicBlock> method, boolean isGlobal){
		// System.out.println("//////////////// NEW METHOD ////////////");
		// System.out.println("Blocks in method: " + method);
		
		if(method.isEmpty()){
			return;
		}
		//clear everything in ctx that needs to be cleared 
		ctx.resetAssignStmtCount(); //reset AssignStmtCount sets it to = globalAssCount
		ctx.getAssignStmtToInt().clear();
		ctx.getIntToAssignStmt().clear();
		ctx.getVarToDefs().clear();

		//clearing these is not necessary
		//if we don't clear, then we will have IN and OUT for most bbs
		//will need to renumber vars to get var->bit
		// ctx.getRdIn().clear();
		// ctx.getRdOut().clear();
		// ctx.getRdGen().clear();
		// ctx.getRdKill().clear();

		if(!isGlobal){
			//if its not global
			//initialize all the maps w/ global defs and variables
			ctx.getAssignStmtToInt().putAll(ctx.getGlobalAssignStmtToInt());
			ctx.getIntToAssignStmt().putAll(ctx.getGlobalIntToAssignStmt());
			ctx.getVarToDefs().putAll(ctx.getGlobalVarToDefs());
		}

		//number definitions
		for(BasicBlock block : method){
			block.numberDefinitions(ctx);
		}
		if(isGlobal){
			//save number of global definitions
			ctx.setGlobalAssignStmtCount(ctx.getAssignStmtCount());
		}

		//make intToAssignStmt
		for(Optimizable stmt : ctx.getAssignStmtToInt().keySet()){
			ctx.getIntToAssignStmt().put(ctx.getAssignStmtToInt().get(stmt), stmt);
		}

		//create map of variables to numberDefinitions
		for(BasicBlock block : method){
			block.findVarToDefs(ctx);
		}

		//make copies maps to save
		HashMap<Optimizable, Integer> assignStmtToInt_copy = new HashMap<>();
		HashMap<Integer, Optimizable> intToAssignStmt_copy = new HashMap<>();
		HashMap<VariableDescriptor, Set<Integer>> varToDefs_copy = new HashMap<>();

		for(Optimizable key : ctx.getAssignStmtToInt().keySet()){
			assignStmtToInt_copy.put(key, ctx.getAssignStmtToInt().get(key));
		}
		for(Integer key : ctx.getIntToAssignStmt().keySet()){
			intToAssignStmt_copy.put(key, ctx.getIntToAssignStmt().get(key));
		}
		for(VariableDescriptor key : ctx.getVarToDefs().keySet()){
			varToDefs_copy.put(key, ctx.getVarToDefs().get(key));
		}

		//populate maps for RD use outside this function
		for(BasicBlock block : method){
			ctx.getBbVarToDefs().put(block, varToDefs_copy);
			ctx.getBbAssToInt().put(block, assignStmtToInt_copy);
			ctx.getBbIntToAss().put(block, intToAssignStmt_copy);
		}

		//if it is global, save map clones also in ctx.global*
		if(isGlobal){
			ctx.getGlobalAssignStmtToInt().putAll(assignStmtToInt_copy);
			ctx.getGlobalIntToAssignStmt().putAll(intToAssignStmt_copy);
			ctx.getGlobalVarToDefs().putAll(varToDefs_copy);
		}

		// System.out.println("AssignStmtToInt-----------");
		// System.out.println(ctx.prettyPrintAssignStmtToInt());
		// System.out.println("bbAssToInt: " + ctx.getBbAssToInt());

		// System.out.println("VarToDefs-----------------");
		// System.out.println(ctx.prettyPrintVarToDefs());

		//for each basic block, instantiate gen and kill sets
		for(BasicBlock block : method){
			block.makeGenSet(ctx);
			block.makeKillSet(ctx);
		}

		// System.out.println("Gen -----------------");
		// System.out.println(ctx.getRdGen().toString());

		// System.out.println("Kill -----------------");
		// System.out.println(ctx.getRdKill().toString());

		//calculate in and out sets 
		for(BasicBlock block : method){
			ctx.getRdOut().put(block, new BitSet(ctx.getAssignStmtCount()));
		}
		BasicBlock entryBlock = method.get(0);
		ctx.getRdIn().put(entryBlock, new BitSet(ctx.getAssignStmtCount()));
		ctx.getRdOut().put(entryBlock, ctx.getRdGen().get(entryBlock));

		Set<BasicBlock> changed = new HashSet<>(method);
		Set<BasicBlock> allBlocksInMethod = new HashSet<>(method);
		changed.remove(entryBlock);

		while(!changed.isEmpty()){
			BasicBlock n = changed.iterator().next();

			// System.out.println("________________________________");
			// System.out.println("currentBlock: " + n.toString());

			changed.remove(n);
			
			ctx.getRdIn().put(n, new BitSet(ctx.getAssignStmtCount())); //IN[n] = emptyset

			for (BasicBlock p : n.getPreviousBlocks()){
				if(allBlocksInMethod.contains(p)){
					//System.out.println("previous " + p);
					BitSet in_n = (BitSet)ctx.getRdIn().get(n).clone();
					BitSet out_p = (BitSet)ctx.getRdOut().get(p).clone();
					in_n.or(out_p);
					ctx.getRdIn().put(n, in_n);
				}
			}

			BitSet in = (BitSet)ctx.getRdIn().get(n).clone();
			BitSet kill = (BitSet)ctx.getRdKill().get(n).clone();
			BitSet gen = (BitSet)ctx.getRdGen().get(n).clone();

			// System.out.println("in: " + in);
			// System.out.println("gen: " + gen);
			// System.out.println("kill: " + kill);

			in.andNot(kill);
			gen.or(in);
			BitSet new_out = gen;

			BitSet old_out = ctx.getRdOut().get(n);
			ctx.getRdOut().put(n, new_out);

			if (!old_out.equals(new_out)){
				for(BasicBlock s : n.getNextBlocks()){
					if(allBlocksInMethod.contains(s)){
						//System.out.println("next " + s);
						changed.add(s);
					}
				}
			}
		}


		// System.out.println("In -----------------");
		// System.out.println(ctx.getRdIn().toString());

		// System.out.println("Out -----------------");
		// System.out.println(ctx.getRdOut().toString());

		//in/out done! 
	}

	// this calculates reachindDefs w/in each method, and does constant/copy propagation
	public void doReachingDefinitions(){
		//System.out.println("DOING REACHING DEFS");

		//list of methods; contain list of basic blocks in methods
		List<List<BasicBlock>> methods = getMethods(orderedBlocks);

		//do thing with global blocks 
		//(if there are no global blocks, it does nothing)
		reachingDefsHelper(ctx.getGlobalBlocks(), true);


		//for each method, instantiate bit vecotrs
		//then, do propagation 

		//CTX regains all info PER METHOD. Loses info once new method entered
		for(List<BasicBlock> method : methods){

			reachingDefsHelper(method, false);

			// // System.out.println("//////////////// NEW METHOD ////////////");
			// // System.out.println("Blocks in method: " + method);
			
			// if(method.isEmpty()){
			// 	continue;
			// }
			// //clear everything in ctx that needs to be cleared 
			// ctx.resetAssignStmtCount();
			// ctx.getAssignStmtToInt().clear();
			// ctx.getIntToAssignStmt().clear();
			// ctx.getVarToDefs().clear();

			// //clearing these is not necessary
			// //if we don't clear, then we will have IN and OUT for most bbs
			// //will need to renumber vars to get var->bit
			// // ctx.getRdIn().clear();
			// // ctx.getRdOut().clear();
			// // ctx.getRdGen().clear();
			// // ctx.getRdKill().clear();

			// //number definitions
			// for(BasicBlock block : method){
			// 	block.numberDefinitions(ctx);
			// }

			// //make intToAssignStmt
			// for(Optimizable stmt : ctx.getAssignStmtToInt().keySet()){
			// 	ctx.getIntToAssignStmt().put(ctx.getAssignStmtToInt().get(stmt), stmt);
			// }

			// //create map of variables to numberDefinitions
			// for(BasicBlock block : method){
			// 	block.findVarToDefs(ctx);
			// }

			// //make copies maps to save
			// HashMap<Optimizable, Integer> assignStmtToInt_copy = new HashMap<>();
			// HashMap<Integer, Optimizable> intToAssignStmt_copy = new HashMap<>();
			// HashMap<VariableDescriptor, Set<Integer>> varToDefs_copy = new HashMap<>();

			// for(Optimizable key : ctx.getAssignStmtToInt().keySet()){
			// 	assignStmtToInt_copy.put(key, ctx.getAssignStmtToInt().get(key));
			// }
			// for(Integer key : ctx.getIntToAssignStmt().keySet()){
			// 	intToAssignStmt_copy.put(key, ctx.getIntToAssignStmt().get(key));
			// }
			// for(VariableDescriptor key : ctx.getVarToDefs().keySet()){
			// 	varToDefs_copy.put(key, ctx.getVarToDefs().get(key));
			// }

			// //populate maps for RD use outside this function
			// for(BasicBlock block : method){
			// 	ctx.getBbVarToDefs().put(block, varToDefs_copy);
			// 	ctx.getBbAssToInt().put(block, assignStmtToInt_copy);
			// 	ctx.getBbIntToAss().put(block, intToAssignStmt_copy);
			// }

			// // System.out.println("AssignStmtToInt-----------");
			// // System.out.println(ctx.prettyPrintAssignStmtToInt());
			// // System.out.println("bbAssToInt: " + ctx.getBbAssToInt());

			// // System.out.println("VarToDefs-----------------");
			// // System.out.println(ctx.prettyPrintVarToDefs());

			// //for each basic block, instantiate gen and kill sets
			// for(BasicBlock block : method){
			// 	block.makeGenSet(ctx);
			// 	block.makeKillSet(ctx);
			// }

			// // System.out.println("Gen -----------------");
			// // System.out.println(ctx.getRdGen().toString());

			// // System.out.println("Kill -----------------");
			// // System.out.println(ctx.getRdKill().toString());

			// //calculate in and out sets 
			// for(BasicBlock block : method){
			// 	ctx.getRdOut().put(block, new BitSet(ctx.getAssignStmtCount()));
			// }
			// BasicBlock entryBlock = method.get(0);
			// ctx.getRdIn().put(entryBlock, new BitSet(ctx.getAssignStmtCount()));
			// ctx.getRdOut().put(entryBlock, ctx.getRdGen().get(entryBlock));

			// Set<BasicBlock> changed = new HashSet<>(method);
			// Set<BasicBlock> allBlocksInMethod = new HashSet<>(method);
			// changed.remove(entryBlock);

			// while(!changed.isEmpty()){
			// 	BasicBlock n = changed.iterator().next();

			// 	// System.out.println("________________________________");
			// 	// System.out.println("currentBlock: " + n.toString());

			// 	changed.remove(n);
				
			// 	ctx.getRdIn().put(n, new BitSet(ctx.getAssignStmtCount())); //IN[n] = emptyset

			// 	for (BasicBlock p : n.getPreviousBlocks()){
			// 		if(allBlocksInMethod.contains(p)){
			// 			//System.out.println("previous " + p);
			// 			BitSet in_n = (BitSet)ctx.getRdIn().get(n).clone();
			// 			BitSet out_p = (BitSet)ctx.getRdOut().get(p).clone();
			// 			in_n.or(out_p);
			// 			ctx.getRdIn().put(n, in_n);
			// 		}
			// 	}

			// 	BitSet in = (BitSet)ctx.getRdIn().get(n).clone();
			// 	BitSet kill = (BitSet)ctx.getRdKill().get(n).clone();
			// 	BitSet gen = (BitSet)ctx.getRdGen().get(n).clone();

			// 	// System.out.println("in: " + in);
			// 	// System.out.println("gen: " + gen);
			// 	// System.out.println("kill: " + kill);

			// 	in.andNot(kill);
			// 	gen.or(in);
			// 	BitSet new_out = gen;

			// 	BitSet old_out = ctx.getRdOut().get(n);
			// 	ctx.getRdOut().put(n, new_out);

			// 	if (!old_out.equals(new_out)){
			// 		for(BasicBlock s : n.getNextBlocks()){
			// 			if(allBlocksInMethod.contains(s)){
			// 				//System.out.println("next " + s);
			// 				changed.add(s);
			// 			}
			// 		}
			// 	}
			// }


			// // System.out.println("In -----------------");
			// // System.out.println(ctx.getRdIn().toString());

			// // System.out.println("Out -----------------");
			// // System.out.println(ctx.getRdOut().toString());

			// //in/out done! 
		}
	}

	public void doGlobalConstantPropagation(){
		//do constant propagation
		for(BasicBlock block : orderedBlocks){
			ctx.setCurrentBlock(block);
			block.doGlobalConstantPropagation(ctx);
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
