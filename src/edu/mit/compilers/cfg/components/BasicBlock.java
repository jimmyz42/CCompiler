package edu.mit.compilers.cfg.components;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.mit.compilers.cfg.CFGAble;
import edu.mit.compilers.cfg.Condition;
import edu.mit.compilers.highir.descriptor.Descriptor;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.optimizer.Optimizable;
import edu.mit.compilers.optimizer.OptimizerContext;

//TODO: delete
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

public class BasicBlock extends CFG {

	private List<Optimizable> components;
	private List<BasicBlock> prevBlocks;
	private List<BasicBlock> nextBlocks;
	private Condition branchCondition;
	private String id;
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

	public void generateTemporaries(OptimizerContext octx) {
		List<Optimizable> newComponents = new ArrayList<>();

		for(Optimizable component: components) {
			newComponents.addAll(component.generateTemporaries(octx));
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

			if(compGen.isEmpty() ||
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
		ctx.getCSEExprToVar().clear();
		ctx.getCSEVarToExprs().clear();

		for(Optimizable component: components) {
			component.doCSE(ctx);
		}
	}

	public void doConstantPropagation(OptimizerContext ctx) {
		ctx.getVarToConst().clear();

		for(Optimizable component: components) {
			component.doConstantPropagation(ctx);
		}
	}

	public void doCopyPropagation(OptimizerContext ctx){
		ctx.getCPTempToVar().clear();
		ctx.getCPVarToSet().clear();
		
		for(Optimizable component: components) {
			System.out.println("__________________START______________________");

			StringWriter sw = new StringWriter();
			component.cfgPrint(new PrintWriter(sw), "");
			System.out.println("Before CP: " + sw.toString());

			component.doCopyPropagation(ctx);
			
			sw = new StringWriter();
			component.cfgPrint(new PrintWriter(sw), "");
			System.out.println("After CP: " + sw.toString());




			System.out.println("TempToVar: " + ctx.getCPTempToVar().toString());
			System.out.println();
			System.out.println("VarToSet: " + ctx.getCPVarToSet().toString());

			System.out.println("____________________END____________________");
		}
	}
	
	public void doAlgebraicSimplification() {
		for(int i=0;i<components.size();i++) {
			components.set(i, components.get(i).algebraSimplify());
		}
	}
}
