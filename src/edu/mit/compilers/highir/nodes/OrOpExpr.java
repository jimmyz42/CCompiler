package edu.mit.compilers.highir.nodes;

import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.Register;
import edu.mit.compilers.lowir.instructions.Or;
import edu.mit.compilers.optimizer.Optimizable;
import exceptions.TypeMismatchError;

public class OrOpExpr extends BinOpExpr {
    public OrOpExpr(BinOp operator, Expression lhs, Expression rhs) {
        super(operator, lhs, rhs);
    }

    public static OrOpExpr create(DecafSemanticChecker checker, DecafParser.OrOpExprContext ctx) {
        OrOp operator = OrOp.create(checker, ctx.OR_OP());
        Expression lhs = Expression.create(checker, ctx.expr(0));
        Expression rhs = Expression.create(checker, ctx.expr(1));

        if (lhs.getType() != ScalarType.BOOL) {
            throw new TypeMismatchError("Left argument of || must be an bool, got a " +
                    lhs.getType(), ctx.expr(0));
        }
        if (rhs.getType() != ScalarType.BOOL) {
            throw new TypeMismatchError("Right argument of || must be an bool, got a " +
                    rhs.getType(), ctx.expr(1));
        }

        return new OrOpExpr(operator, lhs, rhs);
    }

    @Override
    public Type getType() {
        return ScalarType.BOOL;
    }

    @Override
    public BasicBlock shortCircuit(CFG trueBranch, CFG falseBranch) {
    	CFG temp = rhs.shortCircuit(trueBranch, falseBranch);
    	return lhs.shortCircuit(trueBranch, temp);
    }

    @Override
    public void generateAssembly(AssemblyContext ctx){
        lhs.generateAssembly(ctx);
        rhs.generateAssembly(ctx);

        Register src = rhs.allocateRegister(ctx);
        Register dest = lhs.allocateRegister(ctx);
        ctx.addInstruction(new Or(src, dest));

        ctx.storeStack(getStorageTuple(), dest);
        ctx.deallocateRegister(src);
        ctx.deallocateRegister(dest);
    }

	@Override
	public long getNumStackAllocations() {
		return lhs.getNumStackAllocations() + rhs.getNumStackAllocations() + 1;
	}

	@Override
	public Optimizable doAlgebraicSimplification() {
		lhs = (Expression) lhs.doAlgebraicSimplification();
		rhs = (Expression) rhs.doAlgebraicSimplification();
		if(lhs instanceof BoolLiteral && rhs instanceof BoolLiteral) {
			return new BoolLiteral(((BoolLiteral)lhs).getValue() || ((BoolLiteral)rhs).getValue());
		}
		// true || a: true, false || a: a
		if(lhs instanceof BoolLiteral && ((BoolLiteral)lhs).getValue()) {
			return new BoolLiteral(true);
		}
		if(rhs instanceof BoolLiteral && ((BoolLiteral)rhs).getValue()) {
			return new BoolLiteral(true);
		}
		if(lhs instanceof BoolLiteral && !((BoolLiteral)lhs).getValue()) {
			return rhs;
		}
		if(rhs instanceof BoolLiteral && !((BoolLiteral)rhs).getValue()) {
			return lhs;
		}
		return this;
	}
	
	
	@Override
	public Expression clone() {
		return new OrOpExpr(new OrOp(operator.getTerminal()), lhs.clone(), rhs.clone());
	}
}