package edu.mit.compilers.cfg.components;

import java.util.ArrayList;

public class ProgramPoint {
    private ArrayList<BasicBlock> blocks;

    public ProgramPoint(ArrayList<BasicBlock> blocks) {
        this.blocks = blocks;
    }

    public ArrayList<BasicBlock> getBlocks() {
        return blocks;
    }

    public static ProgramPoint create(ArrayList<BasicBlock> blocks) {
        return new ProgramPoint(blocks);
    }

    public static ProgramPoint create (BasicBlock block) {
        ArrayList<BasicBlock> blocks = new ArrayList<>();
        blocks.add(block);
        return ProgramPoint.create(blocks);
    }

    public static ProgramPoint create() {
        return ProgramPoint.create(new ArrayList<>());
    }
}
