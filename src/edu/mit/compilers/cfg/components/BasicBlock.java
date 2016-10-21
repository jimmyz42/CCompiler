package edu.mit.compilers.cfg.components;

import java.util.List;
import java.util.ArrayList;
import java.io.PrintWriter;

import edu.mit.compilers.cfg.CFGAble;
import edu.mit.compilers.highir.nodes.Statement;

public class BasicBlock implements CFGAble {

    private List<CFGAble> components;
    private ProgramPoint entryPoint;
    private ProgramPoint exitPoint;

    public BasicBlock(List<CFGAble> components) {
        this.components = components;
    }

    public static BasicBlock create(List<CFGAble> components) {
        return new BasicBlock(new ArrayList<>());
    }

    public static BasicBlock create(CFGAble component) {
        ArrayList<CFGAble> components = new ArrayList<>();
        components.add(component);
        return new BasicBlock(components);
    }

    @Override
    public BasicBlock generateCFG() {
        return BasicBlock.create(this);
    }

    @Override
    public void concisePrint(PrintWriter pw, String prefix) {
        for(CFGAble component : components) {
            component.concisePrint(pw, prefix);
        }
    }
}