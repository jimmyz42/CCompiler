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

        Storage src = rhs.allocateRegister(ctx);
        //Storage dest = lhs.allocateRegister(ctx);
        Storage result = ctx.allocateRegister(this);
        expression.add(Mov.create(lhs.getLocation(ctx), result));
        if(operator.getTerminal().equals("*")) {
            expression.add(new Imul(src, result));
        } else {
        	//HELP WHAT DO HERE???
            Register rdx = Register.create("%rdx");
            Register rax = Register.create("%rax");
            expression.add(new Xor(rdx, rdx));
            expression.add(new Mov(src, rax));
            expression.add(new Idiv(result));
            expression.add(new Mov(rax, src));
        }
        ctx.addInstructions(expression);
        
        ctx.deallocateRegister(this);
        rhs.deallocateRegister(ctx);
        lhs.deallocateRegister(ctx);
    }

	@Override
	public int getNumStackAllocations() {
		return lhs.getNumStackAllocations() + rhs.getNumStackAllocations() + 1;
	}
}