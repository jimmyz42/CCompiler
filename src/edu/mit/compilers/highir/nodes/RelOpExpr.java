package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.mit.compilers.cfg.Condition;
import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.lowir.Register;
import edu.mit.compilers.lowir.Storage;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.ImmediateValue;
import edu.mit.compilers.lowir.instructions.Cmp;
import edu.mit.compilers.lowir.instructions.Mov;
import edu.mit.compilers.lowir.instructions.Cmovg;
import edu.mit.compilers.lowir.instructions.Cmovl;
import edu.mit.compilers.lowir.instructions.Cmovge;
import edu.mit.compilers.lowir.instructions.Cmovle;
import edu.mit.compilers.lowir.instructions.Instruction;
import exceptions.TypeMismatchError;

public class RelOpExpr extends BinOpExpr implements Condition {
	public RelOpExpr(BinOp operator, Expression lhs, Expression rhs) {
		super(operator, lhs, rhs);
	}

	public static RelOpExpr create(DecafSemanticChecker checker, DecafParser.RelOpExprContext ctx) {
		RelOp operator = RelOp.create(checker, ctx.REL_OP());
		Expression lhs = Expression.create(checker, ctx.expr(0));
		Expression rhs = Expression.create(checker, ctx.expr(1));

		if (lhs.getExpressionType() != ScalarType.INT) {
			throw new TypeMismatchError("Left argument of " + operator + " must be an int, got a " +
					lhs.getExpressionType(), ctx.expr(0));
		}
		if (rhs.getExpressionType() != ScalarType.INT) {
			throw new TypeMismatchError("Right argument of " + operator + " must be an int, got a " +
					rhs.getExpressionType(), ctx.expr(1));
		}

		return new RelOpExpr(operator, lhs, rhs);
	}

	@Override
	public Type getExpressionType() {
		return ScalarType.BOOL;
	}

	@Override
	public BasicBlock shortCircuit(CFG trueBranch, CFG falseBranch) {
		BasicBlock block = BasicBlock.createWithCondition(this);
		block.setNextBlocks(Arrays.asList(trueBranch.getEntryBlock(), falseBranch.getEntryBlock()));
		trueBranch.addPreviousBlock(block);
		falseBranch.addPreviousBlock(block);
		return block;
	}

	@Override
	public void generateAssembly(AssemblyContext ctx) {
		lhs.generateAssembly(ctx);
		rhs.generateAssembly(ctx);

		List<Instruction> expression = new ArrayList<>();

		Register src = rhs.allocateRegister(ctx);
		Register dest = lhs.allocateRegister(ctx);
		Register result = ctx.allocateRegister(getStorageTuple());

		Storage btrue = ImmediateValue.create(true);
		Storage bfalse = ImmediateValue.create(false);

		//move 0 into result
		expression.add(Mov.create(bfalse, result));

		//move 1 into temp
		Storage temp = Register.create("%rax");
		expression.add(Mov.create(btrue, temp));

		//compare lhs to rhs
		Instruction opInstruction = new Cmp(src, dest);
		expression.add(opInstruction);

		switch(operator.getTerminal()) {
		case ">":
			expression.add(Cmovg.create(temp, result));
			break;
		case "<":
			expression.add(Cmovl.create(temp, result));
			break;
		case ">=":
			expression.add(Cmovge.create(temp, result));
			break;
		case "<=":
			expression.add(Cmovle.create(temp, result));
			break;
		default:

		}
		ctx.addInstructions(expression);

		//result = 0 or 1
		//	0: expression evaluated to false
		//	1: expression evaluated to true

		ctx.storeStack(getStorageTuple(), result);
        ctx.deallocateRegister(src);
        ctx.deallocateRegister(dest);
		ctx.deallocateRegister(result);
	}

	@Override
	public long getNumStackAllocations() {
		return lhs.getNumStackAllocations() + rhs.getNumStackAllocations() + 1;
	}
}
