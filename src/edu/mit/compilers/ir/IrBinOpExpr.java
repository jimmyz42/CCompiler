package edu.mit.compilers.ir;

import java.io.PrintWriter;

import edu.mit.compilers.grammar.DecafParser.ExprContext;
import exceptions.TypeMismatchError;


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
        super.prettyPrint(pw, prefix);
        lhs.prettyPrint(pw, prefix + "  ");
        pw.println(prefix + "  " + operator);
        rhs.prettyPrint(pw, prefix + "  ");
    }
}
