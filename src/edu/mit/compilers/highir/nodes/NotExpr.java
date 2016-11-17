package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.highir.descriptor.Descriptor;
import edu.mit.compilers.highir.descriptor.VariableDescriptor;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.Register;
import edu.mit.compilers.lowir.instructions.Mov;
import edu.mit.compilers.lowir.instructions.Not;
import edu.mit.compilers.optimizer.Optimizable;
import edu.mit.compilers.optimizer.OptimizerContext;
import exceptions.TypeMismatchError;

public class NotExpr extends Expression {
    private Expression expression;

    public NotExpr(Expression expression) {
        this.expression = expression;
    }

    public static NotExpr create(DecafSemanticChecker checker, DecafParser.NotExprContext ctx) {
        Expression expression = (Expression) checker.visit(ctx.expr());
        if (expression.getType() != ScalarType.BOOL) {
            throw new TypeMismatchError("Expected a bool expression", ctx.expr());
        }

        return new NotExpr(expression);
    }

    @Override
    public Type getType() {
        // TODO Auto-generated method stub
        return ScalarType.BOOL;
    }

    @Override
    public BasicBlock shortCircuit(CFG trueBranch, CFG falseBranch) {
    	return expression.shortCircuit(falseBranch, trueBranch);
    }

    @Override
    public void cfgPrint(PrintWriter pw, String prefix) {
    	pw.print(prefix + "!");
    	expression.cfgPrint(pw, "");
    }

    @Override
    public void generateAssembly(AssemblyContext ctx){
        expression.generateAssembly(ctx);

        Register src = expression.allocateRegister(ctx);
        Register dest = ctx.allocateRegister(getStorageTuple());
        ctx.addInstruction(Mov.create(src, dest));
        ctx.addInstruction(Not.create(dest));

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
	public List<Optimizable> generateTemporaries(OptimizerContext context) {
		List<Optimizable> temps = new ArrayList<>();

		temps.addAll(expression.generateTemporaries(context));

		if(context.addExpression(expression)) {
			VariableDescriptor temp = context.getExprToTemp().get(expression);
			temps.add(temp);
		}

		VariableDescriptor temp = context.getExprToTemp().get(expression);
		temps.add(AssignStmt.create(IdLocation.create(temp), "=", expression));

		return temps;
	}

	@Override
	public void doCSE(OptimizerContext ctx) {
		VariableDescriptor temp = ctx.getCSEExprToVar().get(expression);

		if(temp != null) {
			expression = new IdLocation(temp);
			ctx.getCSEExprToVar().put(expression, temp);

		} else {
			expression.doCSE(ctx);
		}
	}

	@Override
    public int hashCode() {
        return ("not" + expression.hashCode()).hashCode();
    }

	@Override
	public boolean equals(Object obj) {
		return hashCode() == obj.hashCode();
	}
}