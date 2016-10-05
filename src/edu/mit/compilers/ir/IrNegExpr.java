package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;
import exceptions.TypeMismatchError;

class IrNegExpr extends IrExpression {
    private IrExpression expression;

    public IrNegExpr(IrExpression expression) {
        this.expression = expression;
    }

    public static IrNegExpr create(DecafSemanticChecker checker, DecafParser.NegExprContext ctx) {
        IrExpression expression = (IrExpression) checker.visit(ctx.expr());
        if (expression.getExpressionType() != TypeScalar.INT) {
            throw new TypeMismatchError("Expected an int expression", ctx.expr());
        }

        return new IrNegExpr(expression);
    }

    @Override
    public Type getExpressionType() {
        return TypeScalar.INT;
    }
}