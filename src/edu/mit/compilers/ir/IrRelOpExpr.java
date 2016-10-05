package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;
import exceptions.TypeMismatchError;

public class IrRelOpExpr extends IrBinOpExpr {
    public IrRelOpExpr(IrBinOp operator, IrExpression lhs, IrExpression rhs) {
        super(operator, lhs, rhs);
    }

    public static IrRelOpExpr create(DecafSemanticChecker checker, DecafParser.RelOpExprContext ctx) {
        IrRelOp operator = IrRelOp.create(checker, ctx.REL_OP());
        IrExpression lhs = IrExpression.create(checker, ctx.expr(0));
        IrExpression rhs = IrExpression.create(checker, ctx.expr(1));

        if (lhs.getExpressionType() != TypeScalar.INT) {
            throw new TypeMismatchError("Left argument of " + operator + " must be an int, got a " +
                    lhs.getExpressionType(), ctx.expr(0));
        }
        if (rhs.getExpressionType() != TypeScalar.INT) {
            throw new TypeMismatchError("Right argument of " + operator + " must be an int, got a " +
                    rhs.getExpressionType(), ctx.expr(1));
        }

        return new IrRelOpExpr(operator, lhs, rhs);
    }

    @Override
    public Type getExpressionType() {
        return TypeScalar.BOOL;
    }
}