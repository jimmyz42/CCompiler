package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;
import exceptions.TypeMismatchError;

public class IrOrOpExpr extends IrBinOpExpr {
    public IrOrOpExpr(IrBinOp operator, IrExpression lhs, IrExpression rhs) {
        super(operator, lhs, rhs);
    }

    public static IrOrOpExpr create(DecafSemanticChecker checker, DecafParser.OrOpExprContext ctx) {
        IrOrOp operator = IrOrOp.create(checker, ctx.OR_OP());
        IrExpression lhs = IrExpression.create(checker, ctx.expr(0));
        IrExpression rhs = IrExpression.create(checker, ctx.expr(1));

        if (lhs.getExpressionType() != TypeScalar.BOOL) {
            throw new TypeMismatchError("Left argument of || must be an bool, got a " +
                    lhs.getExpressionType(), ctx.expr(0));
        }
        if (rhs.getExpressionType() != TypeScalar.BOOL) {
            throw new TypeMismatchError("Right argument of || must be an bool, got a " +
                    rhs.getExpressionType(), ctx.expr(1));
        }
        
        return new IrOrOpExpr(operator, lhs, rhs);
    }

    @Override
    public Type getExpressionType() {
        return TypeScalar.BOOL;
    }
}