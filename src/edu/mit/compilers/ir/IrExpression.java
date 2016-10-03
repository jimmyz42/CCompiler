package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;

abstract class IrExpression extends Ir {
    //TODO: move these variables to the appropriate subclasses
    private boolean negative;
    private boolean negation;

    public static IrExpression create(DecafSemanticChecker checker, DecafParser.ExprContext ctx) {
        return (IrExpression) checker.visit(ctx);
    }
}