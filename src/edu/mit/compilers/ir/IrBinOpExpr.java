package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;

abstract public class IrBinOpExpr extends IrExpression {
    private IrBinOp operator;
    private IrExpression lhs;
    private IrExpression rhs;

    public IrBinOpExpr(IrBinOp operator, IrExpression lhs, IrExpression rhs) {
        this.operator = operator;
        this.lhs = lhs;
        this.rhs = rhs;
    }
}
