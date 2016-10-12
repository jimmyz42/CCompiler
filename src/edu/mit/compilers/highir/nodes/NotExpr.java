package edu.mit.compilers.highir.nodes;

import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import exceptions.TypeMismatchError;

public class NotExpr extends Expression {
    private Expression expression;

    public NotExpr(Expression expression) {
        this.expression = expression;
    }

    public static NotExpr create(DecafSemanticChecker checker, DecafParser.NotExprContext ctx) {
        Expression expression = (Expression) checker.visit(ctx.expr());
        if (expression.getExpressionType() != ScalarType.BOOL) {
            throw new TypeMismatchError("Expected a bool expression", ctx.expr());
        }

        return new NotExpr(expression);
    }

    @Override
    public Type getExpressionType() {
        // TODO Auto-generated method stub
        return ScalarType.BOOL;
    }
}