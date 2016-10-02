package edu.mit.compilers.ir;

public class IrBinOpExpr extends IrExpression {
    private IrBinOp operator;
    private IrExpression lhs;
    private IrExpression rhs;
}