package edu.mit.compilers.cfg.components;

import java.io.PrintWriter;
import edu.mit.compilers.cfg.CFGAble;
import edu.mit.compilers.cfg.components.BasicBlock;

public class CFG implements CFGAble {
    private BasicBlock entryBlock;
    private BasicBlock exitBlock;
    private ProgramPoint entryPoint;
    private ProgramPoint exitPoint;

    public CFG(BasicBlock entryBlock, BasicBlock exitBlock) {
        this.entryBlock = entryBlock;
        this.exitBlock = exitBlock;
        this.entryPoint = ProgramPoint.create();
        this.exitPoint = ProgramPoint.create();
    }

    public CFG() {
    }

    public void setEntryPoint(ProgramPoint entryPoint) {
        this.entryPoint = entryPoint;
    }

    public void setExitPoint(ProgramPoint exitPoint) {
        this.exitPoint = exitPoint;
    }

    public BasicBlock getEntryBlock() {
        return entryBlock;
    }

    public BasicBlock getExitBlock() {
        return exitBlock;
    }

    public ProgramPoint getEntryPoint() {
        return entryPoint;
    }

    public ProgramPoint getExitPoint() {
        return exitPoint;
    }

    @Override
    public CFG generateCFG() {
        return this;
    }

    @Override
    public void concisePrint(PrintWriter pw, String prefix) {
        BasicBlock currentBlock = getEntryBlock();
        do {
            currentBlock.concisePrint(pw, prefix);

            //TODO: Recurse and print path through CFG, visiting each basic block exactly once
            // this code only looks at the first branch
            if(getExitPoint().getBlocks().size() > 0) {
                currentBlock = getExitPoint().getBlocks().get(0);
            }
            else {
                break;
            }
        } while(currentBlock.getExitPoint() != null);
    }
}
