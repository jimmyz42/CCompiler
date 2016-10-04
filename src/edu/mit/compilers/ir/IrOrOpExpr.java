package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;

public class IrOrOpExpr extends IrLogicalOpExpr {
    public IrOrOpExpr(IrBinOp operator, IrExpression lhs, IrExpression rhs) {
        super(operator, lhs, rhs);
    }

    public static IrOrOpExpr create(DecafSemanticChecker checker, DecafParser.OrOpExprContext ctx) {
        IrOrOp operator = IrOrOp.create(checker, ctx.OR_OP());
        IrExpression lhs = IrExpression.create(checker, ctx.expr(0));
        IrExpression rhs = IrExpression.create(checker, ctx.expr(1));
        return new IrOrOpExpr(operator, lhs, rhs);
    }
}