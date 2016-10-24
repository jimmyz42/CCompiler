package edu.mit.compilers.cfg.components;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.HashSet;

import edu.mit.compilers.cfg.CFGAble;
import edu.mit.compilers.cfg.CFGContext;
import edu.mit.compilers.cfg.components.BasicBlock;

public class CFG implements CFGAble {
    protected BasicBlock entryBlock;
    protected BasicBlock exitBlock;

    public CFG(BasicBlock entryBlock, BasicBlock exitBlock) {
        this.entryBlock = entryBlock;
        this.exitBlock = exitBlock;
    }

    // For purposes of creating a NOP start and a NOP end
    // to link to, note that entry and exit block are not linked
    // when creating the actual interior CFG link to the NOPs appropriately
    public CFG() {
    	this.entryBlock = BasicBlock.createEmpty();
    	this.exitBlock = BasicBlock.createEmpty();
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
    
    public void addPreviousBlocks(List<BasicBlock> prevBlocks) {
    	List<BasicBlock> blocks = new ArrayList<>();
    	blocks.addAll(getPreviousBlocks());
    	blocks.addAll(prevBlocks);
    	setPreviousBlocks(blocks);
    }
    
    public void addPreviousBlock(BasicBlock prevBlock) {
    	addPreviousBlocks(Collections.singletonList(prevBlock));
    }

    @Override
    public CFG generateCFG(CFGContext context) {
        return this;
    }

    @Override
    public void cfgPrint(PrintWriter pw, String prefix) {
        HashSet<BasicBlock> visited = new HashSet<BasicBlock>();
        Stack<BasicBlock> blockStack = new Stack<>();
        blockStack.push(getEntryBlock());

        while(!blockStack.empty()) {
            BasicBlock currentBlock = blockStack.pop();
            if(visited.contains(currentBlock)) continue;
            else visited.add(currentBlock);

            currentBlock.cfgPrint(pw, prefix + "    ");

            //push blocks in reverse order to pop in correct order
            if(currentBlock.getNextBlocks().size() > 1) {
                blockStack.push(currentBlock.getNextBlock(false));
            }
            if(currentBlock.getNextBlocks().size() > 0){
                blockStack.push(currentBlock.getNextBlock(true));
            }
        }
    }
}
