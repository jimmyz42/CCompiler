package edu.mit.compilers.optimizer;

import java.util.List;
import java.util.Set;

import edu.mit.compilers.cfg.CFGAble;
import edu.mit.compilers.highir.descriptor.Descriptor;
import edu.mit.compilers.lowir.AssemblyContext;

public interface Optimizable extends CFGAble {
	public void generateAssembly(AssemblyContext ctx);
    public List<Optimizable> generateTemporaries(OptimizerContext context);
    public Set<Descriptor> getConsumedDescriptors();
    public Set<Descriptor> getGeneratedDescriptors();
    public void doCSE(OptimizerContext ctx);
    public void doCopyPropagation(OptimizerContext ctx);

}
