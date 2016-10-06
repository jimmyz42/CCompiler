package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;
import exceptions.TypeMismatchError;

public class IrAddOpExpr extends IrBinOpExpr {
    public IrAddOpExpr(IrBinOp operator, IrExpression lhs, IrExpression rhs) {
        super(operator, lhs, rhs);
    }

    public static IrAddOpExpr create(DecafSemanticChecker checker, DecafParser.AddOpExprContext ctx) {
        IrAddOp operator = ctx.PLUS_OP() != null ? IrAddOp.create(checker, ctx.PLUS_OP()) : IrAddOp.create(checker, ctx.DASH());
        IrExpression lhs = IrExpression.create(checker, ctx.expr(0));
        IrExpression rhs = IrExpression.create(checker, ctx.expr(1));

        if (lhs.getExpressionType() != TypeScalar.INT) {
            throw new TypeMismatchError("Left argument of + must be an int, got a " +
                    lhs.getExpressionType(), ctx.expr(0));
        }
        if (rhs.getExpressionType() != TypeScalar.INT) {
            throw new TypeMismatchError("Right argument of + must be an int, got a " +
                    rhs.getExpressionType(), ctx.expr(1));
        }
        
        return new IrAddOpExpr(operator, lhs, rhs);
    }

    @Override
    public Type getExpressionType() {
        return TypeScalar.INT;
    }
}