package edu.mit.compilers.highir.nodes;

import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.Register;
import edu.mit.compilers.lowir.instructions.And;
import edu.mit.compilers.lowir.instructions.Instruction;
import edu.mit.compilers.lowir.instructions.Mov;
import edu.mit.compilers.optimizer.Optimizable;
import exceptions.TypeMismatchError;

public class AndOpExpr extends BinOpExpr {
	public AndOpExpr(BinOp operator, Expression lhs, Expression rhs) {
		super(operator, lhs, rhs);
	}

	public static AndOpExpr create(DecafSemanticChecker checker, DecafParser.AndOpExprContext ctx) {
		AndOp operator = AndOp.create(checker, ctx.AND_OP());
		Expression lhs = Expression.create(checker, ctx.expr(0));
		Expression rhs = Expression.create(checker, ctx.expr(1));

		if (lhs.getType() != ScalarType.BOOL) {
			throw new TypeMismatchError("Left argument of && must be an bool, got a " +
					lhs.getType(), ctx.expr(0));
		}
		if (rhs.getType() != ScalarType.BOOL) {
			throw new TypeMismatchError("Right argument of && must be an bool, got a " +
					rhs.getType(), ctx.expr(1));
		}

		return new AndOpExpr(operator, lhs, rhs);
	}

	@Override
	public Type getType() {
		return ScalarType.BOOL;
	}

	@Override
	public BasicBlock shortCircuit(CFG trueBranch, CFG falseBranch) {
		CFG temp = rhs.shortCircuit(trueBranch, falseBranch);
		return lhs.shortCircuit(temp, falseBranch);
	}

	@Override
	public void generateAssembly(AssemblyContext ctx){
		lhs.generateAssembly(ctx);
		rhs.generateAssembly(ctx);

		Register src = rhs.allocateRegister(ctx);
		Register result = ctx.allocateRegister(getStorageTuple());
		ctx.addInstruction(Mov.create(lhs.getLocation(ctx), result));
		Instruction opInstruction = new And(src, result);
		ctx.addInstruction(opInstruction);

		ctx.storeStack(getStorageTuple(), result);

		ctx.storeStack(getStorageTuple(), result);
		ctx.deallocateRegister(src);
		ctx.deallocateRegister(result);
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
			return new BoolLiteral(((BoolLiteral)lhs).getValue() && ((BoolLiteral)rhs).getValue());
		}
		// true && a: a, false && a: false
		if(lhs instanceof BoolLiteral && ((BoolLiteral)lhs).getValue()) {
			return rhs;
		}
		if(rhs instanceof BoolLiteral && ((BoolLiteral)rhs).getValue()) {
			return lhs;
		}
		if(lhs instanceof BoolLiteral && !((BoolLiteral)lhs).getValue()) {
			return new BoolLiteral(false);
		}
		if(rhs instanceof BoolLiteral && !((BoolLiteral)rhs).getValue()) {
			return new BoolLiteral(false);
		}
		return this;
	}
	
	
	@Override
	public Expression clone() {
		return new AndOpExpr(new AndOp(operator.getTerminal()), lhs.clone(), rhs.clone());
	}
}