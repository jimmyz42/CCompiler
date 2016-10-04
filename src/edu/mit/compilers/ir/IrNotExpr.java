package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;
import exceptions.TypeMismatchException;

class IrNotExpr extends IrExpression {
    private IrExpression expression;

    public IrNotExpr(IrExpression expression) {
        this.expression = expression;
        if (expression.getExpressionType() != TypeScalar.BOOL) {
            throw new TypeMismatchException("Expected a bool expression");
        }
    }

    public static IrNotExpr create(DecafSemanticChecker checker, DecafParser.NotExprContext ctx) {
        return new IrNotExpr((IrExpression) checker.visit(ctx.expr()));
    }

    @Override
    public Type getExpressionType() {
        // TODO Auto-generated method stub
        return TypeScalar.BOOL;
    }
}