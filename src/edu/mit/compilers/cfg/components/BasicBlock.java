package edu.mit.compilers.cfg.components;

import java.util.List;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.io.StringWriter;

import edu.mit.compilers.cfg.*;
import edu.mit.compilers.highir.nodes.BoolLiteral;
import edu.mit.compilers.highir.nodes.Statement;
import edu.mit.compilers.lowir.instructions.Instruction;

public class BasicBlock extends CFG {

    private List<CFGAble> components;
    private List<BasicBlock> prevBlocks;
    private List<BasicBlock> nextBlocks;
    private Condition branchCondition;
    private int id;

    public BasicBlock(List<CFGAble> components) {
        this.components = components;
        this.entryBlock = this;
        this.exitBlock = this;
        this.prevBlocks = new ArrayList<>();
        this.nextBlocks = new ArrayList<>();
        this.branchCondition = null;
    }

    public static BasicBlock create(List<CFGAble> components) {
        return new BasicBlock(components);
    }

    public static BasicBlock create(CFGAble component) {
        ArrayList<CFGAble> components = new ArrayList<>();
        components.add(component);
        return BasicBlock.create(components);
    }
    
    public void setID(int id){
    	this.id = id;
    }
    
    public int getID(){
    	return this.id;
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
        this.nextBlocks = nextBlocks;
    }
    
    public void setCondition(Condition branchCondition) {
    	this.branchCondition = branchCondition;
    }
    
    public Condition getCondition() {
    	return branchCondition;
    }

    @Override
    public void cfgPrint(PrintWriter pw, String prefix) {
        for(CFGAble component: components) {
            component.cfgPrint(pw, prefix);
        }
        if (branchCondition != null){
            branchCondition.cfgPrint(pw,prefix);
        }
    }
    
    @Override
    public List<Instruction> generateAssembly() {
    	// TODO generate label, generate assembly for each component,
    	// generate conditional jump (CMP, then JMP)
    	return null;
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
