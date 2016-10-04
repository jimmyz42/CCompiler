package edu.mit.compilers.ir;

import exceptions.TypeMismatchException;

public abstract class IrLogicalOpExpr extends IrBinOpExpr {
    public IrLogicalOpExpr(IrBinOp operator, IrExpression lhs, IrExpression rhs) {
        super(operator, lhs, rhs);
        if (lhs.getType() != Type.BOOL || rhs.getType() != Type.BOOL) {
            throw new TypeMismatchException("Expected bool arguments");
        }
    }

    @Override
    public Type getType() {
        return Type.BOOL;
    }
}