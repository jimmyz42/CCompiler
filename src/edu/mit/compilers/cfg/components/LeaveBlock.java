package edu.mit.compilers.cfg.components;

import java.util.List;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.io.StringWriter;

import edu.mit.compilers.cfg.*;
import edu.mit.compilers.highir.nodes.BoolLiteral;
import edu.mit.compilers.highir.nodes.Statement;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.instructions.Instruction;
import edu.mit.compilers.lowir.instructions.Jmp;
import edu.mit.compilers.lowir.instructions.Label;

public class LeaveBlock extends BasicBlock {
	private long numStackAllocations;

    public LeaveBlock(List<CFGAble> components) {
        super(components);
    }

    public static LeaveBlock create() {
        return new LeaveBlock(new ArrayList<CFGAble>());
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
        pw.println("*end of method*");
    }

    @Override
    public void generateAssembly(AssemblyContext ctx) {
        ctx.leave(numStackAllocations);
    }

    @Override
    public boolean canMerge() {
    	return false;
    }
}
