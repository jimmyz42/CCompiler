package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;
import exceptions.TypeMismatchError;

public class IrMulOpExpr extends IrBinOpExpr {
    public IrMulOpExpr(IrBinOp operator, IrExpression lhs, IrExpression rhs) {
        super(operator, lhs, rhs);
    }

    public static IrMulOpExpr create(DecafSemanticChecker checker, DecafParser.MulOpExprContext ctx) {
        IrMulOp operator = IrMulOp.create(checker, ctx.MUL_OP());
        IrExpression lhs = IrExpression.create(checker, ctx.expr(0));
        IrExpression rhs = IrExpression.create(checker, ctx.expr(1));

        if (lhs.getExpressionType() != TypeScalar.INT) {
            throw new TypeMismatchError("Left argument of * must be an int, got a " +
                    lhs.getExpressionType(), ctx.expr(0));
        }
        if (rhs.getExpressionType() != TypeScalar.INT) {
            throw new TypeMismatchError("Right argument of * must be an int, got a " +
                    rhs.getExpressionType(), ctx.expr(1));
        }
        
        return new IrMulOpExpr(operator, lhs, rhs);
    }

    @Override
    public Type getExpressionType() {
        return TypeScalar.INT;
    }
}