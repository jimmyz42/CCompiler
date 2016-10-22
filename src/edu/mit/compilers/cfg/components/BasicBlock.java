package edu.mit.compilers.cfg.components;

import java.util.List;
import java.util.ArrayList;
import java.io.PrintWriter;

import edu.mit.compilers.cfg.CFGAble;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.highir.nodes.Statement;

public class BasicBlock extends CFG {

    private List<CFGAble> components;

    public BasicBlock(List<CFGAble> components) {
        this.components = components;
    }

    public static BasicBlock create(List<CFGAble> components) {
        return new BasicBlock(components);
    }

    public static BasicBlock create(CFGAble component) {
        ArrayList<CFGAble> components = new ArrayList<>();
        components.add(component);
        return BasicBlock.create(components);
    }

    @Override
    public BasicBlock getEntryBlock() {
        return this;
    }

    @Override
    public BasicBlock getExitBlock() {
        return this;
    }

    @Override
    public void concisePrint(PrintWriter pw, String prefix) {
        for(CFGAble component: components) {
            component.concisePrint(pw, prefix);
        }

        //TODO: Recurse and print path through CFG, visiting each basic block exactly once
        // this code only looks at the first branch
        if(getNextBlocks().size() > 0) {
            getNextBlock().concisePrint(pw, prefix);
        }
    }
}
