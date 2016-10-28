package edu.mit.compilers.highir.nodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.Register;
import edu.mit.compilers.lowir.instructions.And;
import edu.mit.compilers.lowir.instructions.Instruction;
import exceptions.TypeMismatchError;

public class AndOpExpr extends BinOpExpr {
    public AndOpExpr(BinOp operator, Expression lhs, Expression rhs) {
        super(operator, lhs, rhs);
    }

    public static AndOpExpr create(DecafSemanticChecker checker, DecafParser.AndOpExprContext ctx) {
        AndOp operator = AndOp.create(checker, ctx.AND_OP());
        Expression lhs = Expression.create(checker, ctx.expr(0));
        Expression rhs = Expression.create(checker, ctx.expr(1));

        if (lhs.getExpressionType() != ScalarType.BOOL) {
            throw new TypeMismatchError("Left argument of && must be an bool, got a " +
                    lhs.getExpressionType(), ctx.expr(0));
        }
        if (rhs.getExpressionType() != ScalarType.BOOL) {
            throw new TypeMismatchError("Right argument of && must be an bool, got a " +
                    rhs.getExpressionType(), ctx.expr(1));
        }

        return new AndOpExpr(operator, lhs, rhs);
    }

    @Override
    public Type getExpressionType() {
        return ScalarType.BOOL;
    }

    @Override
    public BasicBlock shortCircuit(CFG trueBranch, CFG falseBranch) {
    	CFG temp = rhs.shortCircuit(trueBranch, falseBranch);
    	return lhs.shortCircuit(temp, falseBranch);
    }

    @Override
    public List<Instruction> generateAssembly(AssemblyContext ctx){
        List<Instruction> lhsInst = lhs.generateAssembly(ctx);
        List<Instruction> rhsInst = rhs.generateAssembly(ctx);
        List<Instruction> expression = new ArrayList<>();
        expression.addAll(lhsInst);
        expression.addAll(rhsInst);

        Register src = ctx.allocateRegister(rhs);
        Register dest = ctx.allocateRegister(lhs);
        Instruction opInstruction = new And(src, dest);
        expression.add(opInstruction);

        ctx.pushStack(this, dest);
        ctx.deallocateRegister(rhs);
        ctx.deallocateRegister(lhs);

        return expression;
    }
}