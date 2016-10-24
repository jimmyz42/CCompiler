package edu.mit.compilers.cfg.components;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.mit.compilers.cfg.CFGAble;
import edu.mit.compilers.cfg.components.BasicBlock;

public class CFG implements CFGAble {
    protected BasicBlock entryBlock;
    protected BasicBlock exitBlock;

    public CFG(BasicBlock entryBlock, BasicBlock exitBlock) {
        this.entryBlock = entryBlock;
        this.exitBlock = exitBlock;
    }

    public CFG() {
    }

    public BasicBlock getEntryBlock() {
        return entryBlock;
    }

    public BasicBlock getExitBlock() {
        return exitBlock;
    }

    public BasicBlock getPreviousBlock() {
        return getPreviousBlocks().get(0);
    }

    public BasicBlock getNextBlock() {
        return getNextBlock(true);
    }

    public BasicBlock getNextBlock(boolean condition) {
        if(getNextBlocks().size() == 0)
        return null;
        if(condition) {
        	return exitBlock.getNextBlocks().get(0);
        } else {
        	return exitBlock.getNextBlocks().get(1);
        }
    }

    public List<BasicBlock> getPreviousBlocks() {
        if(getPreviousBlocks().size() == 0)
        return null;
        return entryBlock.getPreviousBlocks();
    }

    public List<BasicBlock> getNextBlocks() {
        return exitBlock.getNextBlocks();
    }

    public void setPreviousBlocks(List<BasicBlock> prevBlocks) {
    	entryBlock.setPreviousBlocks(prevBlocks);
    }

    public void setNextBlocks(List<BasicBlock> nextBlocks) {
    	exitBlock.setNextBlocks(nextBlocks);
    }

    public void setPreviousBlock(BasicBlock prevBlock) {
    	setPreviousBlocks(Collections.singletonList(prevBlock));
    }

    public void setNextBlock(BasicBlock nextBlock) {
    	setNextBlocks(Collections.singletonList(nextBlock));
    }

    @Override
    public CFG generateCFG() {
        return this;
    }

    @Override
    public void concisePrint(PrintWriter pw, String prefix) {
        getEntryBlock().concisePrint(pw, prefix);
    }
}
