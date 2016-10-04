package edu.mit.compilers.ir;

import java.io.PrintWriter;

import edu.mit.compilers.grammar.DecafParser;

abstract class IrLiteral extends IrExpression {
    public static IrLiteral create(DecafSemanticChecker checker, DecafParser.LiteralContext ctx) {
        return (IrLiteral) checker.visit(ctx);
    }
    
    @Override
    public void println(PrintWriter pw, String prefix) {
        pw.print(prefix + this + " ");
        super.println(pw, "");
    }
}