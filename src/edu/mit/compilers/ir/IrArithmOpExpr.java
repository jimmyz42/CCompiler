package edu.mit.compilers.ir;

import exceptions.TypeMismatchError;

abstract public class IrArithmOpExpr extends IrBinOpExpr {
    public IrArithmOpExpr(IrBinOp operator, IrExpression lhs, IrExpression rhs) {
        super(operator, lhs, rhs);
        if (lhs.getExpressionType() != TypeScalar.INT || rhs.getExpressionType() != TypeScalar.INT) {
            throw new TypeMismatchError("Expected int arguments");
        }
    }

    @Override
    public Type getExpressionType() {
        return TypeScalar.INT;
    }
}