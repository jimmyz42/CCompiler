package edu.mit.compilers.cfg;

import java.io.PrintWriter;
import java.util.List;

import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.instructions.Instruction;

public interface CFGAble {
    public abstract CFG generateCFG(CFGContext context);
    public abstract void cfgPrint(PrintWriter pw, String prefix);
    /*
     * -every generateAssembly that allocates a register should also deallocate the register
     * -every generateAssembly function should use `ctx.pushStack` to push a copy of itself
     *  	onto the stack for future expressions that want to use it's value
     */
	public void generateAssembly(AssemblyContext ctx);
    public long getNumStackAllocations();
}
