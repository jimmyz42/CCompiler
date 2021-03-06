package edu.mit.compilers.optimizer;

import java.util.List;
import java.util.Set;
import java.util.BitSet;

import edu.mit.compilers.cfg.CFGAble;
import edu.mit.compilers.highir.descriptor.Descriptor;
import edu.mit.compilers.lowir.AssemblyContext;

public interface Optimizable extends CFGAble {
	public void generateAssembly(AssemblyContext ctx);
    public Set<Descriptor> getConsumedDescriptors();
    public Set<Descriptor> getGeneratedDescriptors();
    public Optimizable doAlgebraicSimplification();
    public boolean isLinearizable();
    public List<Optimizable> generateTemporaries(OptimizerContext context, boolean skipGeneration);
    public void doCSE(OptimizerContext ctx);
    public void doCopyPropagation(OptimizerContext ctx);
    public void doConstantPropagation(OptimizerContext ctx);
    public void numberDefinitions(OptimizerContext ctx);
    public void findVarToDefs(OptimizerContext ctx);
    public void doGlobalConstantPropagation(OptimizerContext ctx);
    public void makeUseSet(OptimizerContext ctx, BitSet use);
    //for DCE, globals, arrays, method calls cannot be eliminated
    public abstract boolean canEliminate();
}
