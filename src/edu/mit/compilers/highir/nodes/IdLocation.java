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
import edu.mit.compilers.highir.descriptor.ScalarVariableDescriptor;
import edu.mit.compilers.highir.descriptor.VariableDescriptor;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.Register;
import edu.mit.compilers.lowir.Storage;
import edu.mit.compilers.optimizer.Optimizable;
import edu.mit.compilers.optimizer.OptimizerContext;
import exceptions.TypeMismatchError;
import exceptions.UndeclaredIdentifierError;

public class IdLocation extends Location {

    public IdLocation(ScalarVariableDescriptor variable) {
        super(variable);
    }

    @Override
    public void prettyPrint(PrintWriter pw, String prefix) {
        pw.println(prefix + getClass().getSimpleName());
        pw.println(prefix + "-name: " + getVariable().getName());
    }

    @Override
    public void cfgPrint(PrintWriter pw, String prefix) {
        pw.print(prefix + getVariable().getName());
    }

    @Override
    public CFG generateCFG(CFGContext context) {
        return BasicBlock.createEmpty();
    }

    public Storage getLocation(AssemblyContext ctx) {
    	return getVariable().getLocation(ctx);
    }

    @Override
    public Register allocateRegister(AssemblyContext ctx) {
    	return getVariable().allocateRegister(ctx);
    }

	@Override
	public Set<Descriptor> getConsumedDescriptors() {
		return Collections.singleton((Descriptor)variable);
	}

	@Override
	public Set<Descriptor> getGeneratedDescriptors() {
		return Collections.singleton((Descriptor)variable);
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
    public void doCopyPropagation(OptimizerContext ctx){
    }

    @Override
    public void doConstantPropagation(OptimizerContext ctx){
    }

	@Override
    public int hashCode() {
        return ("variablelocation" + variable).hashCode();
    }

    public static IdLocation create(DecafSemanticChecker checker, DecafParser.IdLocationContext ctx) {
    	//TODO this check for if a variable is declared is broken
        String varName = ctx.ID().getText();
        ScalarVariableDescriptor variable = (ScalarVariableDescriptor) checker.currentSymbolTable().getVariable(varName, ctx);
        if (variable == null) {
            throw new UndeclaredIdentifierError("Variable ''" + varName + "' is not declared", ctx);
        }
        if (!(variable.getType() instanceof ScalarType)) {
            throw new TypeMismatchError("Expected a scalar variable, got " + variable.getType(), ctx);
        }
        return new IdLocation(variable);
    }

    public static IdLocation create(VariableDescriptor variable) {
        return new IdLocation((ScalarVariableDescriptor) variable);
    }
}
