package edu.mit.compilers.highir.nodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.Register;
import edu.mit.compilers.lowir.Storage;
import edu.mit.compilers.lowir.instructions.Add;
import edu.mit.compilers.lowir.instructions.Sub;
import edu.mit.compilers.lowir.instructions.Instruction;
import edu.mit.compilers.lowir.instructions.Mov;
import exceptions.TypeMismatchError;

public class AddOpExpr extends BinOpExpr {
	public AddOpExpr(BinOp operator, Expression lhs, Expression rhs) {
		super(operator, lhs, rhs);
	}

	public static AddOpExpr create(DecafSemanticChecker checker, DecafParser.AddOpExprContext ctx) {
		AddOp operator = ctx.PLUS_OP() != null ? AddOp.create(checker, ctx.PLUS_OP())
				: AddOp.create(checker, ctx.DASH());
		Expression lhs = Expression.create(checker, ctx.expr(0));
		Expression rhs = Expression.create(checker, ctx.expr(1));

		if (lhs.getType() != ScalarType.INT) {
			throw new TypeMismatchError("Left argument of + must be an int, got a " + lhs.getType(),
					ctx.expr(0));
		}
		if (rhs.getType() != ScalarType.INT) {
			throw new TypeMismatchError("Right argument of + must be an int, got a " + rhs.getType(),
					ctx.expr(1));
		}

		return new AddOpExpr(operator, lhs, rhs);
	}

	@Override
	public Type getType() {
		return ScalarType.INT;
	}

	@Override
	public void generateAssembly(AssemblyContext ctx) {
		lhs.generateAssembly(ctx);
		rhs.generateAssembly(ctx);

		Register src = rhs.allocateRegister(ctx);
		Register result = ctx.allocateRegister(getStorageTuple());
		ctx.addInstruction(Mov.create(lhs.getLocation(ctx), result));

		Instruction opInstruction;

		if (operator.getTerminal().equals("+")) {
			opInstruction = new Add(src, result);
		} else {
			opInstruction = new Sub(src, result);
		}
		ctx.addInstruction(opInstruction);

		ctx.storeStack(getStorageTuple(), result);
		ctx.deallocateRegister(src);
		ctx.deallocateRegister(result);
	}

	@Override
	public long getNumStackAllocations() {
		return lhs.getNumStackAllocations() + rhs.getNumStackAllocations() + 1;
	}
}