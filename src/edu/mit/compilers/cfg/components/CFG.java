package edu.mit.compilers.cfg.components;

import java.io.PrintWriter; 
import edu.mit.compilers.cfg.CFGAble;
import edu.mit.compilers.cfg.components.BasicBlock;

public class CFG implements CFGAble {
    private BasicBlock entryBlock;
    private BasicBlock exitBlock;

    public CFG(BasicBlock entryBlock, BasicBlock exitBlock) {
        this.entryBlock = entryBlock;
        this.exitBlock = exitBlock;
    }

    public CFG(BasicBlock block) {
        this(block, block);
    }

    public BasicBlock getEntryBlock() {
        return entryBlock;
    }

    public BasicBlock getExitBlock() {
        return exitBlock;
    }

    @Override
    public CFG generateCFG() {
        return this;
    }   
    
    @Override
    public void concisePrint(PrintWriter pw, String prefix) {
        //TODO: Recurse and print path through CFG, visiting each basic block exactly once
        entryBlock.concisePrint(pw, prefix);
    }  
}
