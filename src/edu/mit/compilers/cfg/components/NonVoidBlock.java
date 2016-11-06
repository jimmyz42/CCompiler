package edu.mit.compilers.cfg.components;

import java.util.List;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.io.StringWriter;

import edu.mit.compilers.cfg.*;
import edu.mit.compilers.highir.nodes.BoolLiteral;
import edu.mit.compilers.highir.nodes.Statement;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.ImmediateValue;
import edu.mit.compilers.lowir.Memory;
import edu.mit.compilers.lowir.Register;
import edu.mit.compilers.lowir.instructions.Instruction;
import edu.mit.compilers.lowir.instructions.Jmp;
import edu.mit.compilers.lowir.instructions.Label;
import edu.mit.compilers.lowir.instructions.Mov;
import edu.mit.compilers.lowir.instructions.Syscall;

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
