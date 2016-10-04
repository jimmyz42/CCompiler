package edu.mit.compilers.ir;

import exceptions.TypeMismatchException;

public abstract class IrLogicalOpExpr extends IrBinOpExpr {
    public IrLogicalOpExpr(IrBinOp operator, IrExpression lhs, IrExpression rhs) {
        super(operator, lhs, rhs);
        if (lhs.getExpressionType() != TypeScalar.BOOL || rhs.getExpressionType() != TypeScalar.BOOL) {
            throw new TypeMismatchException("Expected bool arguments");
        }
    }

    @Override
    public Type getExpressionType() {
        return TypeScalar.BOOL;
    }
}