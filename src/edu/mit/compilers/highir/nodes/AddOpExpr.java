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

		if (lhs.getExpressionType() != ScalarType.INT) {
			throw new TypeMismatchError("Left argument of + must be an int, got a " + lhs.getExpressionType(),
					ctx.expr(0));
		}
		if (rhs.getExpressionType() != ScalarType.INT) {
			throw new TypeMismatchError("Right argument of + must be an int, got a " + rhs.getExpressionType(),
					ctx.expr(1));
		}

		return new AddOpExpr(operator, lhs, rhs);
	}

	@Override
	public Type getExpressionType() {
		return ScalarType.INT;
	}

	@Override
	public void generateAssembly(AssemblyContext ctx) {
		lhs.generateAssembly(ctx);
		rhs.generateAssembly(ctx);
		
		Storage src = rhs.allocateLocation(ctx);
		Storage dest = lhs.allocateLocation(ctx);
		Instruction opInstruction;
		if (operator.getTerminal().equals("+")) {
			opInstruction = new Add(src, dest);
		} else {
			opInstruction = new Sub(src, dest);
		}
		ctx.addInstruction(opInstruction);

		ctx.pushStack(this, dest);
		rhs.deallocateLocation(ctx);
		lhs.deallocateLocation(ctx);
	}
}