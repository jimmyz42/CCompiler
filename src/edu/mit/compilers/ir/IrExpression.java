package edu.mit.compilers.ir;

import java.io.PrintWriter;

import edu.mit.compilers.grammar.DecafParser;

abstract class IrExpression extends IrExternArg {
    public static IrExpression create(DecafSemanticChecker checker, DecafParser.ExprContext ctx) {
        return (IrExpression) checker.visit(ctx);
    }
    
    public abstract Type getExpressionType();
    
}