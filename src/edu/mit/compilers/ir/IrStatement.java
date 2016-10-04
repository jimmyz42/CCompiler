package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;

abstract class IrStatement extends Ir {
    public static IrStatement create(DecafSemanticChecker checker, DecafParser.StatementContext ctx) {
        return (IrStatement) checker.visit(ctx);
    }
}