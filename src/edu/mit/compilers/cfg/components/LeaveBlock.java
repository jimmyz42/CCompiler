package edu.mit.compilers.cfg.components;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.optimizer.Optimizable;

public class LeaveBlock extends BasicBlock {
	private long numStackAllocations;
	boolean notVoid;
	
    public LeaveBlock(List<Optimizable> components, boolean isNotVoid) {
        super(components);
        this.notVoid = isNotVoid;
    }

    public static LeaveBlock create(boolean isNotVoid) {
        return new LeaveBlock(new ArrayList<Optimizable>(), isNotVoid);
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
