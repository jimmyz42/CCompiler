package edu.mit.compilers.highir.nodes;

import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;

abstract public class Statement extends Ir {
    public static Statement create(DecafSemanticChecker checker, DecafParser.StatementContext ctx) {
        return (Statement) checker.visit(ctx);
    }
}