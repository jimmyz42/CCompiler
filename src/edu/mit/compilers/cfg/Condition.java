package edu.mit.compilers.cfg;

import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import edu.mit.compilers.highir.descriptor.Descriptor;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.optimizer.Optimizable;
import edu.mit.compilers.optimizer.OptimizerContext;

public interface Condition {
	// Represents a boolean condition for jumping
	// TODO generate corresponding jump statements
	// BoolLiteral also implements this TODO generate unconditional jump
	
	public void generateAssembly(AssemblyContext ctx);
	public void cfgPrint(PrintWriter pw, String prefix);
    public long getNumStackAllocations();
    public Set<Descriptor> getConsumedDescriptors();
    public Optimizable doConstantFolding();
    public Optimizable algebraSimplify();
    public List<Optimizable> generateTemporaries(OptimizerContext context, boolean skipGeneration);
    public void doCSE(OptimizerContext ctx);
    public void doCopyPropagation(OptimizerContext ctx);
    public void doConstantPropagation(OptimizerContext ctx);
}
