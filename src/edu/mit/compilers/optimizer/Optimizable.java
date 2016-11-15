package edu.mit.compilers.optimizer;

import java.util.Set;

import edu.mit.compilers.highir.descriptor.Descriptor;
import edu.mit.compilers.lowir.AssemblyContext;

public interface Optimizable {
	public void generateAssembly(AssemblyContext ctx);
    public long getNumStackAllocations();
    public Set<Descriptor> getConsumedDescriptors();
    public Set<Descriptor> getGeneratedDescriptors();
}
