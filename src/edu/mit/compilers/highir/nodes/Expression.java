package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;

import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;

abstract public class Expression extends ExternArg {
    public static Expression create(DecafSemanticChecker checker, DecafParser.ExprContext ctx) {
        return (Expression) checker.visit(ctx);
    }
    
    public abstract Type getExpressionType();
    
}