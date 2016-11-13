package edu.mit.compilers.cfg.components;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.cfg.CFGAble;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.instructions.Directive;
import edu.mit.compilers.lowir.instructions.Label;

public class EnterBlock extends BasicBlock {
	private String name;
	private long numStackAllocations;

    public EnterBlock(String name, List<CFGAble> components) {
        super(components);
        this.name = name;
    }

    public static EnterBlock create(String name) {
        return new EnterBlock(name, new ArrayList<CFGAble>());
    }

    // For detecting NOPs
    public boolean isEmpty() {
        return false;
    }

    public void setNumStackAllocations(long numStackAllocations) {
    	this.numStackAllocations = numStackAllocations;
    }

    @Override
    public void cfgPrint(PrintWriter pw, String prefix) {
        pw.println("*start of method*");
    }

    @Override
    public void generateAssembly(AssemblyContext ctx) {
        ctx.addInstruction(Directive.create(".globl " + name));
        ctx.addInstruction(Directive.create(".type " + name + " @function"));
        ctx.addInstruction(Label.create(name));
        ctx.enter(numStackAllocations);
    }

    @Override
    public boolean canMerge() {
    	return false;
    }
}
