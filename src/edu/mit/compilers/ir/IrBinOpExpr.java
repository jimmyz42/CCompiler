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
    public void prettyPrint(PrintWriter pw, String prefix) {
        pw.println(prefix + getClass().getSimpleName());
        pw.println(prefix + "-lhs:");
        lhs.prettyPrint(pw, prefix + "    ");
        pw.println(prefix + "-operator: " + operator);
        pw.println(prefix + "-rhs:");
        rhs.prettyPrint(pw, prefix + "    ");
    }
}
