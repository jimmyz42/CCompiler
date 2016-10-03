package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;

abstract class IrLocation extends IrExpression {
    public static IrLocation create(DecafSemanticChecker checker, DecafParser.LocationContext ctx) {
        return (IrLocation) checker.visit(ctx);
    }
}