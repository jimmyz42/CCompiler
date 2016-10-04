package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;

class IrReturnStmt extends IrStatement {
    private final IrExpression expression;
    
    public IrReturnStmt(IrExpression expression) {
        this.expression = expression;
    }
    
    public static IrReturnStmt create(DecafSemanticChecker checker, DecafParser.ReturnStmtContext ctx) {
        return new IrReturnStmt(IrExpression.create(checker, ctx.expr()));
    }
}