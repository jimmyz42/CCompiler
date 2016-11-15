package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;
import java.util.ArrayList;
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
import edu.mit.compilers.highir.descriptor.VariableDescriptor;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.Register;
import edu.mit.compilers.lowir.instructions.Mov;
import edu.mit.compilers.lowir.instructions.Neg;
import edu.mit.compilers.optimizer.OptimizerContext;
import exceptions.TypeMismatchError;

public class NegExpr extends Expression {
    private Expression expression;

    public NegExpr(Expression expression) {
        this.expression = expression;
    }

    public static NegExpr create(DecafSemanticChecker checker, DecafParser.NegExprContext ctx) {
        Expression expression = (Expression) checker.visit(ctx.expr());
        if (expression.getType() != ScalarType.INT) {
            throw new TypeMismatchError("Expected an int expression", ctx.expr());
        }

        return new NegExpr(expression);
    }

    @Override
    public Type getType() {
        return ScalarType.INT;
    }

    @Override
    public void prettyPrint(PrintWriter pw, String prefix){
    	super.prettyPrint(pw, prefix);
    	pw.print(prefix + "-expression:");
    	expression.prettyPrint(pw, prefix + "    ");
    }

    @Override
    public void cfgPrint(PrintWriter pw, String prefix) {
    	pw.print(prefix + "-");
    	expression.cfgPrint(pw, "");
    }

    @Override
    public void generateAssembly(AssemblyContext ctx){
        expression.generateAssembly(ctx);

        Register src = expression.allocateRegister(ctx);
        Register dest = ctx.allocateRegister(getStorageTuple());
        ctx.addInstruction(Mov.create(src, dest));
        ctx.addInstruction(Neg.create(dest));

		ctx.storeStack(getStorageTuple(), dest);
        ctx.deallocateRegister(src);
        ctx.deallocateRegister(dest);
    }

	@Override
	public long getNumStackAllocations() {
		return expression.getNumStackAllocations() + 1;
	}

	@Override
	public Set<Descriptor> getConsumedDescriptors() {
		return expression.getConsumedDescriptors();
	}

	@Override
	public Set<Descriptor> getGeneratedDescriptors() {
		return Collections.emptySet();
	}

	@Override
	public List<CFGAble> generateTemporaries(OptimizerContext context) {
		List<CFGAble> temps = new ArrayList<>();
		
    	temps.addAll(expression.generateTemporaries(context));
    	VariableDescriptor temp = context.addExpression(expression);
    	temps.add(temp);
    	temps.add(AssignStmt.create(IdLocation.create(temp), "=", this));
    	
        return temps;
	}
	
	@Override
    public int hashCode() {
        return -expression.hashCode();
    }
}