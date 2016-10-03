package edu.mit.compilers.ir;

public class IrBinOpExpr extends IrExpression {
    private IrBinOp operator;
    private IrExpression lhs;
    private IrExpression rhs;

    public IrBinOpExpr(IrExpression lhs, IrBinOp operator, IrExpression rhs) {
        super();
        this.operator = operator;
        this.lhs = lhs;
        this.rhs = rhs;
    }
}