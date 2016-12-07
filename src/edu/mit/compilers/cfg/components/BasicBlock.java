package edu.mit.compilers.cfg.components;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.BitSet;

import edu.mit.compilers.cfg.CFGAble;
import edu.mit.compilers.cfg.Condition;
import edu.mit.compilers.highir.descriptor.Descriptor;
import edu.mit.compilers.highir.nodes.AssignStmt;
import edu.mit.compilers.highir.nodes.BoolLiteral;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.optimizer.Optimizable;
import edu.mit.compilers.optimizer.OptimizerContext;

public class BasicBlock extends CFG {

	private List<Optimizable> components;
	private List<BasicBlock> prevBlocks;
	private List<BasicBlock> nextBlocks;
	private Condition branchCondition;
	private String id; // id = "block" + numID
	private int numID;
	private String description;

	public BasicBlock(List<Optimizable> components) {
		this.components = components;
		this.entryBlock = this;
		this.exitBlock = this;
		this.prevBlocks = new ArrayList<>();
		this.nextBlocks = new ArrayList<>();
		this.branchCondition = null;
		this.description = "";
	}

	public static BasicBlock create(List<Optimizable> components) {
		return new BasicBlock(components);
	}

	public static BasicBlock create(Optimizable component) {
		ArrayList<Optimizable> components = new ArrayList<>();
		components.add(component);
		return BasicBlock.create(components);
	}
	
	public void setNumID(int num) {
		this.numID = num;
	}
	
	public int getNumID() {
		return this.numID;
	}

	public void setID(String id){
		this.id = id;
	}

	public String getID(){
		return this.id;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}

	public static BasicBlock createEmpty(String description) {
		BasicBlock block = new BasicBlock(new ArrayList<Optimizable>());
		block.setDescription(description);
		return block;
	}

	public static BasicBlock createEmpty() {
		return new BasicBlock(new ArrayList<Optimizable>());
	}

	public static BasicBlock createWithCondition(Condition condition) {
		BasicBlock block = BasicBlock.createEmpty();
		block.branchCondition = condition;
		return block;
	}

	// For detecting NOPs
	public boolean isEmpty() {
		return components.size() == 0 && branchCondition == null;
	}

	@Override
	public List<BasicBlock> getPreviousBlocks() {
		return prevBlocks;
	}

	@Override
	public List<BasicBlock> getNextBlocks() {
		return nextBlocks;
	}

	@Override
	public void setPreviousBlocks(List<BasicBlock> prevBlocks) {
		this.prevBlocks = prevBlocks;
	}

	@Override
	public void setNextBlocks(List<BasicBlock> nextBlocks) {
		if(this.nextBlocks.size() == 0) this.nextBlocks = nextBlocks;
	}

	public void setCondition(Condition branchCondition) {
		this.branchCondition = branchCondition;
	}

	public Condition getCondition() {
		return branchCondition;
	}

	public void cfgPrint(PrintWriter pw, String prefix) {
		if(description.length() > 0) {
			pw.println("*"+description+"*");
		}
		for(CFGAble component: components) {
			component.cfgPrint(pw, prefix);
		}
		if (branchCondition != null){
			branchCondition.cfgPrint(pw,prefix);
		}
		pw.println();
	}

	public void generateAssembly(AssemblyContext ctx) {
		for(CFGAble component: components) {
			component.generateAssembly(ctx);
		}
		if(branchCondition != null) {
			branchCondition.generateAssembly(ctx);
		}
	}

	@Override
	public long getNumStackAllocations() {
		int numStackAllocations = 0;
		for(CFGAble component: components) {
			numStackAllocations += component.getNumStackAllocations();
		}
		if(branchCondition != null) {
			numStackAllocations += branchCondition.getNumStackAllocations();
		}

		return numStackAllocations;
	}

	public boolean canMerge() {
		return true;
	}

	// Checks for merging precondition
	public static boolean canMerge(BasicBlock b1, BasicBlock b2) {
		if(b1.getNextBlocks().size() != 1) return false;
		if(b2.getPreviousBlocks().size() != 1) return false;
		if(b1.getNextBlock() != b2) return false;
		if(b2.getPreviousBlock() != b1) return false;
		if(!b1.canMerge()) return false;
		if(!b2.canMerge()) return false;
		return true;
	}

	// Precondition: b1.getNextBlocks() = [b2], b2.getPreviousBlocks() = [b1]
	// Aka b1 only points to b2, b2 is only pointed to by b1
	public static BasicBlock merge(BasicBlock b1, BasicBlock b2) {
		List<Optimizable> both = new ArrayList<>();
		both.addAll(b1.components);
		both.addAll(b2.components);
		BasicBlock combined = BasicBlock.create(both);
		combined.setPreviousBlocks(b1.getPreviousBlocks());
		combined.setNextBlocks(b2.getNextBlocks());
		combined.setCondition(b2.getCondition());
		return combined;
	}
	
	public void doAlgebraicSimplification() {
		for(int i=0;i<components.size();i++) {
			components.set(i, components.get(i).doAlgebraicSimplification());
		}
//		if(branchCondition != null) {
//			branchCondition = (Condition) branchCondition.doAlgebraicSimplification();
//		}
	}

	public void generateTemporaries(OptimizerContext octx) {
		List<Optimizable> newComponents = new ArrayList<>();

		for(Optimizable component: components) {
			newComponents.addAll(component.generateTemporaries(octx, false));
		}
		if(branchCondition != null) {
			newComponents.addAll(branchCondition.generateTemporaries(octx, false));
		}

		this.components = newComponents;
	}


	public HashSet<Descriptor> doDeadCodeEliminiation(HashSet<Descriptor> consumed) {
		List<Optimizable> deadComponents = new ArrayList<>();

		if(branchCondition != null) {
			consumed.addAll(branchCondition.getConsumedDescriptors());
		}
		for(int i = components.size() -1; i >= 0; i--) {
			Optimizable component = components.get(i);
			
			Set<Descriptor> compGen = component.getGeneratedDescriptors();
			Set<Descriptor> compCon = component.getConsumedDescriptors();
			
			if(component instanceof AssignStmt && ((AssignStmt) component).isReflexive()) {
				deadComponents.add(component);
			} else if(compGen.isEmpty() ||
					!Collections.disjoint(consumed, compGen) ||
					!Collections.disjoint(compGen, compCon)) {
				consumed.addAll(compCon);
			} else
				deadComponents.add(component);
		}

		this.components.removeAll(deadComponents);
		return consumed;
	}

	public void doCSE(OptimizerContext ctx) {
		for(Optimizable component: components) {
			component.doCSE(ctx);
		}
		if(branchCondition != null) {
			branchCondition.doCSE(ctx);
		}
	}

	public void doGlobalConstantPropagation(OptimizerContext ctx) {
		for(Optimizable component: components) {
			component.doGlobalConstantPropagation(ctx);
		}
		if(branchCondition != null) {
			branchCondition.doGlobalConstantPropagation(ctx);
		}
	}

	public void doConstantPropagation(OptimizerContext ctx) {
		ctx.getVarToConst().clear();

		for(Optimizable component: components) {
			component.doConstantPropagation(ctx);
		}
		if(branchCondition != null) {
			branchCondition.doConstantPropagation(ctx);
		}
	}

	public void doCopyPropagation(OptimizerContext ctx){
		ctx.getCPTempToVar().clear();
		ctx.getCPVarToSet().clear();
		
		for(Optimizable component: components) {
			component.doCopyPropagation(ctx);
		}
		if(branchCondition != null) {
			branchCondition.doCopyPropagation(ctx);
		}
	}
	
	public void numberDefinitions(OptimizerContext ctx){
		for(Optimizable component : components){
			if(component instanceof AssignStmt){
				component.numberDefinitions(ctx);
			}
		}
	}

	public void findVarToDefs(OptimizerContext ctx){
		for(Optimizable component : components){
			if(component instanceof AssignStmt){
				component.findVarToDefs(ctx);
			}
		}
	}

	public void makeGenSet(OptimizerContext ctx){
		BitSet rdGen = new BitSet(ctx.getAssignStmtCount());
		for(int i = components.size()-1; i>=0; i--){ //go through backwards 
			if(components.get(i) instanceof AssignStmt){
				AssignStmt stmt = (AssignStmt)components.get(i);
				stmt.makeGenSet(ctx, rdGen);
			}
		}
		ctx.getRdGen().put(this, rdGen);
	}

	public void makeKillSet(OptimizerContext ctx){
		BitSet rdKill = new BitSet(ctx.getAssignStmtCount());
		for(Optimizable component : components){
			if(component instanceof AssignStmt){
				AssignStmt stmt = (AssignStmt)component;
				stmt.makeKillSet(ctx, rdKill);
			}
		}
		ctx.getRdKill().put(this, rdKill);
	}

	public void doUnreachableCodeElimination() { 
		if(branchCondition instanceof BoolLiteral) {
			BasicBlock leftBlock = getNextBlock(true);
			BasicBlock rightBlock = getNextBlock(false);
			nextBlocks = Collections.singletonList((((BoolLiteral) branchCondition).getValue()? leftBlock:rightBlock));
			branchCondition = null;
		}
	}

	@Override
	public String toString(){
		return id;
	}
}
