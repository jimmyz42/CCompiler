package edu.mit.compilers.highir.nodes;

import java.util.Set;

import edu.mit.compilers.cfg.CFGContext;
import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.grammar.DecafParser.LocationContext;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.highir.descriptor.Descriptor;
import edu.mit.compilers.highir.descriptor.VariableDescriptor;
import edu.mit.compilers.lowir.AssemblyContext;

abstract public class Location extends Expression {
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
}