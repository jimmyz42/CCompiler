package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;

class IrNegExpr extends Ir {
    private IrExpression expression;

    public IrNegExpr(IrExpression expression) {
        this.expression = expression;
    }

    public static IrNegExpr create(DecafSemanticChecker checker, DecafParser.NegExprContext ctx) {
        return new IrNegExpr((IrExpression) checker.visit(ctx.expr()));
    }
}