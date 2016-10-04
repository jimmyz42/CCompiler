package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;

public class IrAndOpExpr extends IrBinOpExpr {
    private IrBinOp operator;
    private IrExpression lhs;
    private IrExpression rhs;

    public IrAndOpExpr(IrBinOp operator, IrExpression lhs, IrExpression rhs) {
        super(operator, lhs, rhs);
    }

    public static IrAndOpExpr create(DecafSemanticChecker checker, DecafParser.AndOpExprContext ctx) {
        IrAndOp operator = IrAndOp.create(checker, ctx.AND_OP());
        IrExpression lhs = IrExpression.create(checker, ctx.expr(0));
        IrExpression rhs = IrExpression.create(checker, ctx.expr(1));
        return new IrAndOpExpr(operator, lhs, rhs);
    }
}