package edu.mit.compilers.ir;

import java.io.PrintWriter;

import edu.mit.compilers.grammar.DecafParser;

class IrReturnStmt extends IrStatement {
    private final IrExpression expression;
    
    public IrReturnStmt(IrExpression expression) {
        this.expression = expression;
    }
    
    public static IrReturnStmt create(DecafSemanticChecker checker, DecafParser.ReturnStmtContext ctx) {
        return new IrReturnStmt(IrExpression.create(checker, ctx.expr()));
    }
    
    @Override
    public void prettyPrint(PrintWriter pw, String prefix) {
        super.prettyPrint(pw, prefix);
        pw.println(prefix + "-return:");
        expression.prettyPrint(pw, prefix + "    ");
    }
}