package edu.mit.compilers.cfg.components;

import java.util.List;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.io.StringWriter;

import edu.mit.compilers.cfg.CFGAble;
import edu.mit.compilers.cfg.Condition;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.highir.nodes.BoolLiteral;
import edu.mit.compilers.highir.nodes.Statement;

public class BasicBlock extends CFG {

    private List<CFGAble> components;
    private List<BasicBlock> prevBlocks;
    private List<BasicBlock> nextBlocks;
    private Condition branchCondition;

    public BasicBlock(List<CFGAble> components) {
        this.components = components;
        this.entryBlock = this;
        this.exitBlock = this;
        this.prevBlocks = new ArrayList<>();
        this.nextBlocks = new ArrayList<>();
        this.branchCondition = new BoolLiteral(true);
    }

    public static BasicBlock create(List<CFGAble> components) {
        return new BasicBlock(components);
    }

    public static BasicBlock create(CFGAble component) {
        ArrayList<CFGAble> components = new ArrayList<>();
        components.add(component);
        return BasicBlock.create(components);
    }

    public static BasicBlock create() {
        return new BasicBlock(new ArrayList<CFGAble>());
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

    @Override
    public void concisePrint(PrintWriter pw, String prefix) {
        for(CFGAble component: components) {
            component.concisePrint(pw, prefix);
        }

        //TODO: Recurse and print path through CFG, visiting each basic block exactly once
        // this code only looks at the first branch
        if(getNextBlock() != null) {
            System.out.println("new block");
            System.out.println(getNextBlock());
            System.out.println(this);
            getNextBlock().concisePrint(pw, prefix);
        }
    }
}
