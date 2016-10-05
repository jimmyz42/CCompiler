package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;

public class IrAddOpExpr extends IrArithmOpExpr {
    public IrAddOpExpr(IrBinOp operator, IrExpression lhs, IrExpression rhs) {
        super(operator, lhs, rhs);
    }

    public static IrAddOpExpr create(DecafSemanticChecker checker, DecafParser.AddOpExprContext ctx) {
        IrAddOp operator = ctx.PLUS_OP() != null ? IrAddOp.create(checker, ctx.PLUS_OP()) : IrAddOp.create(checker, ctx.DASH());
        IrExpression lhs = IrExpression.create(checker, ctx.expr(0));
        IrExpression rhs = IrExpression.create(checker, ctx.expr(1));
        return new IrAddOpExpr(operator, lhs, rhs);
    }
}