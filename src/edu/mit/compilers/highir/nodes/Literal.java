package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import edu.mit.compilers.cfg.CFGContext;
import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.highir.descriptor.Descriptor;
import edu.mit.compilers.optimizer.Optimizable;
import edu.mit.compilers.optimizer.OptimizerContext;

abstract public class Literal extends Expression {
    public static Literal create(DecafSemanticChecker checker, DecafParser.LiteralContext ctx) {
        return (Literal) checker.visit(ctx);
    }

    @Override
    public void prettyPrint(PrintWriter pw, String prefix) {
        super.prettyPrint(pw, prefix);
    	pw.println(prefix + "-value: " + this);
    }
    
    @Override
    public void cfgPrint(PrintWriter pw, String prefix) {
    	pw.print(prefix + this);
    }

    @Override
    public CFG generateCFG(CFGContext context) {
        return BasicBlock.createEmpty();
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
	public boolean isLinearizable() {
		return true;
	}
	
	@Override
	public Set<Location> getLocationsUsed() {
		return Collections.emptySet();
	}

	@Override
	public List<Optimizable> generateTemporaries(OptimizerContext context, boolean skipGeneration) {
		return Collections.emptyList();
	}

    @Override
    public void doCopyPropagation(OptimizerContext ctx){
    }

    @Override
    public void doConstantPropagation(OptimizerContext ctx){
    }
	
	@Override
	public void doCSE(OptimizerContext ctx) {
	}

	@Override
	public boolean equals(Object obj) {
		return hashCode() == obj.hashCode();
	}
	
	@Override
	public boolean isInvariantStmt(OptimizerContext ctx){
		return true;
	}
	
	@Override
	public boolean canEliminate() {
		return true;
	}
	
	@Override
	public Expression clone() {
		return this;
	}
}