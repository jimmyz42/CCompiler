package edu.mit.compilers.cfg;

import java.io.PrintWriter;

import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.lowir.AssemblyContext;

public interface CFGAble {
    public CFG generateCFG(CFGContext context);
    public void cfgPrint(PrintWriter pw, String prefix);
    public long getNumStackAllocations();
	public void generateAssembly(AssemblyContext ctx);
}
