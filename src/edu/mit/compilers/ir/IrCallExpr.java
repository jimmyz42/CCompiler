package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;

class IrCallExpr extends Ir {
    private IrMethodCall call;

    public IrCallExpr(IrMethodCall call) {
        this.call = call;
    }

    public static IrCallExpr create(DecafSemanticChecker checker, DecafParser.CallExprContext ctx) {
        return new IrCallExpr((IrMethodCall) checker.visit(ctx.method_call()));
    }
}