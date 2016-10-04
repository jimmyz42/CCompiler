package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;

class IrNotExpr extends Ir {
    private IrExpression expression;

    public IrNotExpr(IrExpression expression) {
        this.expression = expression;
    }

    public static IrNotExpr create(DecafSemanticChecker checker, DecafParser.NotExprContext ctx) {
        return new IrNotExpr((IrExpression) checker.visit(ctx.expr()));
    }
}