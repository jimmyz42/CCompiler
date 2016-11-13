package edu.mit.compilers.cfg.components;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.cfg.CFGAble;
import edu.mit.compilers.lowir.AssemblyContext;

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
    
    public boolean getNotVoid(){
    	return this.notVoid;
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
        ctx.leave(numStackAllocations);
    }

    @Override
    public boolean canMerge() {
    	return false;
    }
}
