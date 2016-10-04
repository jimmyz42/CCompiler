package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser.LocationContext;

abstract class IrLocation extends IrExpression {

    public static IrLocation create(DecafSemanticChecker checker, LocationContext ctx) {
        return (IrLocation) checker.visit(ctx);
    }
}