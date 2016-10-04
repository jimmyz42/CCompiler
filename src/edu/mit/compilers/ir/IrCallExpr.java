package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;

class IrCallExpr extends Ir {
    private IrMethodCallExpr call;

    public IrCallExpr(IrMethodCallExpr call) {
        this.call = call;
    }

    public static IrCallExpr create(DecafSemanticChecker checker, DecafParser.CallExprContext ctx) {
        return new IrCallExpr((IrMethodCallExpr) checker.visit(ctx.method_call()));
    }
}