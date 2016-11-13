package edu.mit.compilers.cfg;

import java.io.PrintWriter;
import java.util.Set;

import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.highir.descriptor.Descriptor;
import edu.mit.compilers.lowir.AssemblyContext;

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
    public Set<Descriptor> getConsumedDescriptors();
    public Set<Descriptor> getGeneratedDescriptors();
}
