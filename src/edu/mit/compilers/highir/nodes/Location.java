package edu.mit.compilers.highir.nodes;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.BitSet;

import edu.mit.compilers.cfg.Condition;
import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.grammar.DecafParser.LocationContext;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.highir.descriptor.VariableDescriptor;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.optimizer.OptimizerContext;

abstract public class Location extends Expression implements Condition {
    protected VariableDescriptor variable;

    Location(VariableDescriptor variable) {
        this.variable = variable;
    }

    public static Location create(DecafSemanticChecker checker, LocationContext ctx) {
        return (Location) checker.visit(ctx);
    }

    public VariableDescriptor getVariable() {
        return variable;
    }

    @Override
    public Type getType() {
        return variable.getType();
    }
    
	@Override
	public Set<Location> getLocationsUsed() {
		return Collections.singleton(this);
	}

    @Override
    public BasicBlock shortCircuit(CFG trueBranch, CFG falseBranch) {
    	EqOpExpr expr = new EqOpExpr(new EqOp("=="), this, new BoolLiteral(true));
        return expr.shortCircuit(trueBranch, falseBranch);
    }

    @Override
    public void generateAssembly(AssemblyContext ctx) {
    }

	@Override
	public long getNumStackAllocations() {
		return 0;
	}

    @Override
    public void doCopyPropagation(OptimizerContext ctx) {   
    }

    @Override
    public void makeUseSet(OptimizerContext ctx, BitSet use){
        if(ctx.getLivVarToInt().containsKey(variable)){
            Integer i = ctx.getLivVarToInt().get(variable);
            use.set(i);
        }
    }

    @Override
    public boolean isInvariantStmt(OptimizerContext ctx){
        if(ctx.areRDsOutsideLoop(this.variable)){
            return true;
        }
        return false;
    }

	@Override
	public void doCSE(OptimizerContext ctx) {	
	}
    
	@Override
	public boolean equals(Object obj) {
		return hashCode() == obj.hashCode();
	}
	
	@Override
	public Expression clone() {
		return this;
	}
}