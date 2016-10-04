package edu.mit.compilers.ir;

import java.io.PrintWriter;


abstract public class IrBinOpExpr extends IrExpression {
    private IrBinOp operator;
    private IrExpression lhs;
    private IrExpression rhs;

    public IrBinOpExpr(IrBinOp operator, IrExpression lhs, IrExpression rhs) {
        this.operator = operator;
        this.lhs = lhs;
        this.rhs = rhs;
    }
    
    @Override
    public void println(PrintWriter pw, String prefix) {
        super.println(pw, prefix);
        lhs.println(pw, prefix + "  ");
        pw.println(prefix + "  " + operator);
        rhs.println(pw, prefix + "  ");
    }
}
