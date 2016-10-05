package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;
import exceptions.TypeMismatchError;

class IrNotExpr extends IrExpression {
    private IrExpression expression;

    public IrNotExpr(IrExpression expression) {
        this.expression = expression;
    }

    public static IrNotExpr create(DecafSemanticChecker checker, DecafParser.NotExprContext ctx) {
        IrExpression expression = (IrExpression) checker.visit(ctx.expr());
        if (expression.getExpressionType() != TypeScalar.BOOL) {
            throw new TypeMismatchError("Expected a bool expression");
        }

        return new IrNotExpr(expression);
    }

    @Override
    public Type getExpressionType() {
        // TODO Auto-generated method stub
        return TypeScalar.BOOL;
    }
}