package edu.mit.compilers.cfg.components;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.Memory;
import edu.mit.compilers.lowir.instructions.Jmp;
import edu.mit.compilers.optimizer.Optimizable;

public class NonVoidBlock extends BasicBlock {
	
    public NonVoidBlock(List<Optimizable> components) {
        super(components);
    }

    public static NonVoidBlock create() {
        return new NonVoidBlock(new ArrayList<Optimizable>());
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
