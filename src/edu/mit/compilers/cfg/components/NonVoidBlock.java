package edu.mit.compilers.cfg.components;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.cfg.CFGAble;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.Memory;
import edu.mit.compilers.lowir.instructions.Jmp;

public class NonVoidBlock extends BasicBlock {
	
    public NonVoidBlock(List<CFGAble> components) {
        super(components);
    }

    public static NonVoidBlock create() {
        return new NonVoidBlock(new ArrayList<CFGAble>());
    }

    // For detecting NOPs
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void cfgPrint(PrintWriter pw, String prefix) {
    	pw.println("exit(-2)");
    }

    @Override
    public void generateAssembly(AssemblyContext ctx) {
		ctx.addInstruction(Jmp.create(Memory.create("fall_off_error")));
    }

    @Override
    public boolean canMerge() {
    	return false;
    }
}
