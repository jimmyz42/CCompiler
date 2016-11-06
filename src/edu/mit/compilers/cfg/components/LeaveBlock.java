package edu.mit.compilers.cfg.components;

import java.util.List;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.io.StringWriter;

import edu.mit.compilers.cfg.*;
import edu.mit.compilers.highir.nodes.BoolLiteral;
import edu.mit.compilers.highir.nodes.EndOfNonVoidMethod;
import edu.mit.compilers.highir.nodes.Statement;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.ImmediateValue;
import edu.mit.compilers.lowir.Register;
import edu.mit.compilers.lowir.instructions.Instruction;
import edu.mit.compilers.lowir.instructions.Jmp;
import edu.mit.compilers.lowir.instructions.Label;
import edu.mit.compilers.lowir.instructions.Mov;
import edu.mit.compilers.lowir.instructions.Syscall;

public class LeaveBlock extends BasicBlock {
	private long numStackAllocations;
	boolean notVoid;
	
    public LeaveBlock(List<CFGAble> components, boolean isNotVoid) {
        super(components);
        this.notVoid = isNotVoid;
    }

    public static LeaveBlock create(boolean isNotVoid) {
        return new LeaveBlock(new ArrayList<CFGAble>(), isNotVoid);
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
    	if(notVoid){
            pw.println("NOT VOID");
    	}
        pw.println("*end of method*");
    }

    @Override
    public void generateAssembly(AssemblyContext ctx) {
    	if(notVoid){
        	ctx.addInstruction(Mov.create(ImmediateValue.create(60),Register.create("%rax")));
        	ctx.addInstruction(Mov.create(ImmediateValue.create(-2),Register.create("%rdi")));
        	ctx.addInstruction(Syscall.create());
    	}
        ctx.leave(numStackAllocations);
    }

    @Override
    public boolean canMerge() {
    	return false;
    }
}
