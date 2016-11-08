package edu.mit.compilers.highir.nodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.lowir.Register;
import edu.mit.compilers.lowir.Storage;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.instructions.Imul;
import edu.mit.compilers.lowir.instructions.Cmovg;
import edu.mit.compilers.lowir.instructions.Cmovge;
import edu.mit.compilers.lowir.instructions.Cmovl;
import edu.mit.compilers.lowir.instructions.Cmovle;
import edu.mit.compilers.lowir.instructions.Idiv;
import edu.mit.compilers.lowir.instructions.Mov;
import edu.mit.compilers.lowir.instructions.Xor;
import edu.mit.compilers.lowir.instructions.Instruction;
import exceptions.TypeMismatchError;

public class MulOpExpr extends BinOpExpr {
    public MulOpExpr(BinOp operator, Expression lhs, Expression rhs) {
        super(operator, lhs, rhs);
    }

    public static MulOpExpr create(DecafSemanticChecker checker, DecafParser.MulOpExprContext ctx) {
        MulOp operator = MulOp.create(checker, ctx.MUL_OP());
        Expression lhs = Expression.create(checker, ctx.expr(0));
        Expression rhs = Expression.create(checker, ctx.expr(1));

        if (lhs.getExpressionType() != ScalarType.INT) {
            throw new TypeMismatchError("Left argument of * must be an int, got a " +
            lhs.getExpressionType(), ctx.expr(0));
        }
        if (rhs.getExpressionType() != ScalarType.INT) {
            throw new TypeMismatchError("Right argument of * must be an int, got a " +
            rhs.getExpressionType(), ctx.expr(1));
        }

        return new MulOpExpr(operator, lhs, rhs);
    }

    @Override
    public Type getExpressionType() {
        return ScalarType.INT;
    }

    @Override
    public void generateAssembly(AssemblyContext ctx){
        lhs.generateAssembly(ctx);
        rhs.generateAssembly(ctx);
        List<Instruction> expression = new ArrayList<>();

        Register src = rhs.allocateRegister(ctx);
        Register result = ctx.allocateRegister(getStorageTuple());
        expression.add(Mov.create(lhs.getLocation(ctx), result));
        Register rdx = Register.create("%rdx");
        Register rax = Register.create("%rax");
        switch(operator.getTerminal()) {
		case "*":
            expression.add(new Imul(src, result));
			break;
		case "/":
            expression.add(new Xor(rdx, rdx));
            expression.add(new Mov(result, rax));
            expression.add(new Idiv(src));
            expression.add(new Mov(rax, result));
			break;
		case "%":
            expression.add(new Xor(rdx, rdx));
            expression.add(new Mov(result, rax));
            expression.add(new Idiv(src));
            expression.add(new Mov(rdx, result));
			break;
		default:

		}
        if(operator.getTerminal().equals("*")) {
        } else {
        }
        ctx.addInstructions(expression);

		ctx.storeStack(getStorageTuple(), result);
        ctx.deallocateRegister(src);
        ctx.deallocateRegister(result);
    }

	@Override
	public long getNumStackAllocations() {
		return lhs.getNumStackAllocations() + rhs.getNumStackAllocations() + 1;
	}
}