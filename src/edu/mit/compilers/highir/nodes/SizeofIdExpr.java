package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.highir.descriptor.Descriptor;
import edu.mit.compilers.highir.descriptor.VariableDescriptor;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.ImmediateValue;
import edu.mit.compilers.optimizer.Optimizable;
import edu.mit.compilers.optimizer.OptimizerContext;
import exceptions.UndeclaredIdentifierError;

public class SizeofIdExpr extends Expression {
    private VariableDescriptor variable;

    public SizeofIdExpr(VariableDescriptor variable) {
        this.variable = variable;
    }

    public static SizeofIdExpr create(DecafSemanticChecker checker, DecafParser.SizeofIdExprContext ctx) {
        String varName = ctx.ID().getText();
        VariableDescriptor variable = checker.currentSymbolTable().getVariable(varName, ctx);
        if (variable == null) {
            throw new UndeclaredIdentifierError("Attempted to apply sizeof to an undeclared variable", ctx);
        }

        return new SizeofIdExpr(variable);
    }

    @Override
    public Type getType() {
        return ScalarType.INT;
    }

    @Override
    public void cfgPrint(PrintWriter pw, String prefix) {
    	pw.print(prefix + "sizeof(" + variable.getName() + ")");
    }

    @Override
    public void generateAssembly(AssemblyContext ctx) {
		ctx.storeStack(getStorageTuple(), ImmediateValue.create(this.variable.getType().getSize()));
    }

	@Override
	public long getNumStackAllocations() {
		return 1;
	}

    @Override
    public ImmediateValue getLocation(AssemblyContext ctx) {
        return ImmediateValue.create(this.variable.getType().getSize());
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
	public Optimizable algebraSimplify() {
		return new IntLiteral(variable.getType().getSize());
	}

	@Override
	public boolean isLinearizable() {
		return true;
	}

	@Override
	public List<Optimizable> generateTemporaries(OptimizerContext context, boolean skipGeneration) {
		return Collections.emptyList();
	}

	@Override
	public void doCSE(OptimizerContext ctx) {
	}

    @Override
    public void doCopyPropagation(OptimizerContext ctx){
    }

    @Override
    public void doConstantPropagation(OptimizerContext ctx){
    }

	@Override
    public int hashCode() {
        return ("sizeofid" + ImmediateValue.create(this.variable.getType().getSize()).hashCode()).hashCode();
    }

	@Override
	public boolean equals(Object obj) {
		return hashCode() == obj.hashCode();
	}
}