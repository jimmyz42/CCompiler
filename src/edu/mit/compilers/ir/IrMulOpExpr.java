package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;

public class IrMulOpExpr extends IrArithmOpExpr {
    public IrMulOpExpr(IrBinOp operator, IrExpression lhs, IrExpression rhs) {
        super(operator, lhs, rhs);
    }

    public static IrMulOpExpr create(DecafSemanticChecker checker, DecafParser.MulOpExprContext ctx) {
        IrMulOp operator = IrMulOp.create(checker, ctx.MUL_OP());
        IrExpression lhs = IrExpression.create(checker, ctx.expr(0));
        IrExpression rhs = IrExpression.create(checker, ctx.expr(1));
        return new IrMulOpExpr(operator, lhs, rhs);
    }
}