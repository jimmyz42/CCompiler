package edu.mit.compilers.highir.nodes;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.Register;
import edu.mit.compilers.lowir.instructions.Cqo;
import edu.mit.compilers.lowir.instructions.Idiv;
import edu.mit.compilers.lowir.instructions.Imul;
import edu.mit.compilers.lowir.instructions.Instruction;
import edu.mit.compilers.lowir.instructions.Mov;
import edu.mit.compilers.lowir.instructions.Xor;
import edu.mit.compilers.optimizer.Optimizable;
import exceptions.TypeMismatchError;

public class MulOpExpr extends BinOpExpr {
    public MulOpExpr(BinOp operator, Expression lhs, Expression rhs) {
        super(operator, lhs, rhs);
    }

    public static MulOpExpr create(DecafSemanticChecker checker, DecafParser.MulOpExprContext ctx) {
        MulOp operator = MulOp.create(checker, ctx.MUL_OP());
        Expression lhs = Expression.create(checker, ctx.expr(0));
        Expression rhs = Expression.create(checker, ctx.expr(1));

        if (lhs.getType() != ScalarType.INT) {
            throw new TypeMismatchError("Left argument of * must be an int, got a " +
            lhs.getType(), ctx.expr(0));
        }
        if (rhs.getType() != ScalarType.INT) {
            throw new TypeMismatchError("Right argument of * must be an int, got a " +
            rhs.getType(), ctx.expr(1));
        }

        return new MulOpExpr(operator, lhs, rhs);
    }

    @Override
    public Type getType() {
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
            expression.add(new Mov(result, rax));
            expression.add(new Cqo());
            expression.add(new Idiv(src));
            expression.add(new Mov(rax, result));
			break;
		case "%":
            expression.add(new Mov(result, rax));
            expression.add(new Cqo());
            expression.add(new Idiv(src));
            expression.add(new Mov(rdx, result));
			break;
		default:

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

	@Override
	public Optimizable doAlgebraicSimplification() {
		lhs = (Expression) lhs.doAlgebraicSimplification();
		rhs = (Expression) rhs.doAlgebraicSimplification();
		if(lhs instanceof IntLiteral && rhs instanceof IntLiteral) {
			if(operator.getTerminal().equals("*")) {
				return new IntLiteral(((IntLiteral)lhs).getValue() * ((IntLiteral)rhs).getValue());
			}
			if(operator.getTerminal().equals("/")) {
				return new IntLiteral(((IntLiteral)lhs).getValue() / ((IntLiteral)rhs).getValue());
			}
			if(operator.getTerminal().equals("%")) {
				return new IntLiteral(((IntLiteral)lhs).getValue() % ((IntLiteral)rhs).getValue());
			}
		}
		// a*0 = 0*a = 0, 1*a = a*1 = a/1 = a, -1*a = a*(-1) = a/(-1) = -a, a%1 = a%(-1) = 0
		if(operator.getTerminal().equals("*")) {
			if(lhs instanceof IntLiteral && ((IntLiteral)lhs).getValue() == 0) {
				return new IntLiteral(0);
			}
			if(rhs instanceof IntLiteral && ((IntLiteral)rhs).getValue() == 0) {
				return new IntLiteral(0);
			}
			if(lhs instanceof IntLiteral && ((IntLiteral)lhs).getValue() == 1) {
				return rhs;
			}
			if(rhs instanceof IntLiteral && ((IntLiteral)rhs).getValue() == 1) {
				return lhs;
			}
			if(lhs instanceof IntLiteral && ((IntLiteral)lhs).getValue() == -1) {
				return new NegExpr(rhs);
			}
			if(rhs instanceof IntLiteral && ((IntLiteral)rhs).getValue() == -1) {
				return new NegExpr(lhs);
			}
		} else if(operator.getTerminal().equals("/")) {
			if(rhs instanceof IntLiteral && ((IntLiteral)rhs).getValue() == 1) {
				return lhs;
			}
			if(rhs instanceof IntLiteral && ((IntLiteral)rhs).getValue() == -1) {
				return new NegExpr(lhs);
			}
		} else { // %
			if(rhs instanceof IntLiteral && ((IntLiteral)rhs).getValue() == 1) {
				return new IntLiteral(0);
			}
			if(rhs instanceof IntLiteral && ((IntLiteral)rhs).getValue() == -1) {
				return new IntLiteral(0);
			}
		}
		return this; //cannot simplify
	}
	
	@Override
	public Expression clone() {
		return new MulOpExpr(new MulOp(operator.getTerminal()), lhs.clone(), rhs.clone());
	}
}