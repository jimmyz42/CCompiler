package edu.mit.compilers.cfg;

import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.highir.descriptor.Descriptor;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.optimizer.OptimizerContext;

public interface CFGAble {
    public CFG generateCFG(CFGContext context);
    public void cfgPrint(PrintWriter pw, String prefix);
    public long getNumStackAllocations();
	public void generateAssembly(AssemblyContext ctx);
    public Set<Descriptor> getConsumedDescriptors();
    public Set<Descriptor> getGeneratedDescriptors();
    public List<CFGAble> generateTemporaries(OptimizerContext context);
}
