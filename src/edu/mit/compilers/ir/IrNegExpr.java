package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;
import exceptions.TypeMismatchError;

class IrNegExpr extends IrExpression {
    private IrExpression expression;

    public IrNegExpr(IrExpression expression) {
        this.expression = expression;
        if (expression.getExpressionType() != TypeScalar.INT) {
            throw new TypeMismatchError("Expected an int expression");
        }
    }

    public static IrNegExpr create(DecafSemanticChecker checker, DecafParser.NegExprContext ctx) {
        return new IrNegExpr((IrExpression) checker.visit(ctx.expr()));
    }

    @Override
    public Type getExpressionType() {
        return TypeScalar.INT;
    }
}