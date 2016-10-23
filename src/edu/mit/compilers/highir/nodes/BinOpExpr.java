package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;

import edu.mit.compilers.grammar.DecafParser.ExprContext;
import exceptions.TypeMismatchError;


abstract public class BinOpExpr extends Expression {
    protected BinOp operator;
    protected Expression lhs;
    protected Expression rhs;

    public BinOpExpr(BinOp operator, Expression lhs, Expression rhs) {
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
