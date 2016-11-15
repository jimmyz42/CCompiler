package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import edu.mit.compilers.cfg.CFGAble;
import edu.mit.compilers.cfg.CFGContext;
import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.highir.descriptor.Descriptor;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.optimizer.Optimizable;
import edu.mit.compilers.optimizer.OptimizerContext;

abstract public class Statement extends Ir implements CFGAble, Optimizable {
    public static Statement create(DecafSemanticChecker checker, DecafParser.StatementContext ctx) {
        return (Statement) checker.visit(ctx);
    }

    @Override
    public void cfgPrint(PrintWriter pw, String prefix) {
        pw.println("");
    }
    
    @Override
    public void generateAssembly(AssemblyContext ctx) {
    	
    }
    
    @Override
    public long getNumStackAllocations() {
    	return 0;
    }

	@Override
	public Set<Descriptor> getConsumedDescriptors() {
		return Collections.emptySet();
	}

	@Override
	public Set<Descriptor> getGeneratedDescriptors() {
		return Collections.emptySet();
	}

	@Override
	public List<CFGAble> generateTemporaries(OptimizerContext context) {
		return Collections.emptyList();
	}
}