package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;

public class IrRelOpExpr extends IrBinOpExpr {
    private IrBinOp operator;
    private IrExpression lhs;
    private IrExpression rhs;

    public IrRelOpExpr(IrBinOp operator, IrExpression lhs, IrExpression rhs) {
        super(operator, lhs, rhs);
    }

    public static IrRelOpExpr create(DecafSemanticChecker checker, DecafParser.RelOpExprContext ctx) {
        IrRelOp operator = IrRelOp.create(checker, ctx.REL_OP());
        IrExpression lhs = IrExpression.create(checker, ctx.expr(0));
        IrExpression rhs = IrExpression.create(checker, ctx.expr(1));
        return new IrRelOpExpr(operator, lhs, rhs);
    }
}