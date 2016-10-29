package edu.mit.compilers.highir.nodes;

import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import exceptions.TypeMismatchError;
import java.io.PrintWriter;

import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;

public class NegExpr extends Expression {
    private Expression expression;

    public NegExpr(Expression expression) {
        this.expression = expression;
    }

    public static NegExpr create(DecafSemanticChecker checker, DecafParser.NegExprContext ctx) {
        Expression expression = (Expression) checker.visit(ctx.expr());
        if (expression.getExpressionType() != ScalarType.INT) {
            throw new TypeMismatchError("Expected an int expression", ctx.expr());
        }

        return new NegExpr(expression);
    }

    @Override
    public Type getExpressionType() {
        return ScalarType.INT;
    }
    
    @Override
    public void prettyPrint(PrintWriter pw, String prefix){
    	super.prettyPrint(pw, prefix);
    	pw.println(prefix + "-expression:");
    	expression.prettyPrint(pw, prefix + "    ");
    }

    //TODO generateAssembly
}