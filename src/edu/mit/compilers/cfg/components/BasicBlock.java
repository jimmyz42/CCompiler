package edu.mit.compilers.cfg.components;

import java.util.List;
import java.util.Queue;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.io.PrintWriter;
import java.io.StringWriter;

import edu.mit.compilers.cfg.*;
import edu.mit.compilers.highir.nodes.BoolLiteral;
import edu.mit.compilers.highir.nodes.Statement;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.instructions.Instruction;
import edu.mit.compilers.lowir.instructions.Jmp;
import edu.mit.compilers.lowir.instructions.Label;

public class BasicBlock extends CFG {

    private List<CFGAble> components;
    private List<BasicBlock> prevBlocks;
    private List<BasicBlock> nextBlocks;
    private Condition branchCondition;
    private String id;
    private String description;

    public BasicBlock(List<CFGAble> components) {
        this.components = components;
        this.entryBlock = this;
        this.exitBlock = this;
        this.prevBlocks = new ArrayList<>();
        this.nextBlocks = new ArrayList<>();
        this.branchCondition = null;
        this.description = "";
    }

    public static BasicBlock create(List<CFGAble> components) {
        return new BasicBlock(components);
    }

    public static BasicBlock create(CFGAble component) {
        ArrayList<CFGAble> components = new ArrayList<>();
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
        BasicBlock block = new BasicBlock(new ArrayList<CFGAble>());
        block.setDescription(description);
        return block;
    }

    public static BasicBlock createEmpty() {
        return new BasicBlock(new ArrayList<CFGAble>());
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

    @Override
    public void cfgPrint(PrintWriter pw, String prefix) {
    	if(description.length() > 0) {
    		pw.println("*"+description+"*");
    	}
        for(CFGAble component: components) {
            component.cfgPrint(pw, prefix);
        }
        if (branchCondition != null){
            branchCondition.cfgPrint(pw,prefix);
        } else {
        	pw.println();
        }
    }

    @Override
    public void generateAssembly(AssemblyContext ctx) {
    	for(CFGAble component: components) {
    		component.generateAssembly(ctx);
    	}
    	if(branchCondition != null) {
    		branchCondition.generateAssembly(ctx);
    	}
    }

    public int getNumStackAllocations() {
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
    	List<CFGAble> both = new ArrayList<>();
    	both.addAll(b1.components);
    	both.addAll(b2.components);
    	BasicBlock combined = BasicBlock.create(both);
    	combined.setPreviousBlocks(b1.getPreviousBlocks());
    	combined.setNextBlocks(b2.getNextBlocks());
    	combined.setCondition(b2.getCondition());
        return combined;
    }
}
