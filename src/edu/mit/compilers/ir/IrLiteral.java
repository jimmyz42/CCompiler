package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;

abstract class IrLiteral extends IrExpression {
    public static IrLiteral create(DecafSemanticChecker checker, DecafParser.LiteralContext ctx) {
        return (IrLiteral) checker.visit(ctx);
    }
}