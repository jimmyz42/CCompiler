package edu.mit.compilers.cfg.components;

import java.io.PrintWriter;
import java.util.ArrayList;

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
        this.entryPoint = ProgramPoint.create();
        this.exitPoint = ProgramPoint.create();
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

    public BasicBlock getPreviousBlock() {
        return entryPoint.getBlocks().get(0);
    }

    public BasicBlock getNextBlock() {
        return exitPoint.getBlocks().get(0);
    }

    public BasicBlock getNextBlock(boolean condition) {
        if(condition)
        return exitPoint.getBlocks().get(0);
        else
        return exitPoint.getBlocks().get(1);
    }

    public ArrayList<BasicBlock> getPreviousBlocks() {
        return entryPoint.getBlocks();
    }

    public ArrayList<BasicBlock> getNextBlocks() {
        return exitPoint.getBlocks();
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
