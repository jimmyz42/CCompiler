package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;

class IrWhileStmt extends IrStatement {
    private IrExpression condition;
    private IrBlock block;
    
    public IrWhileStmt(IrExpression condition, IrBlock block) {
        this.condition = condition;
        this.block = block;
    }

    public static IrWhileStmt create(DecafSemanticChecker checker, DecafParser.WhileStmtContext ctx, SymbolTable symbolTable) {
        IrExpression condition = IrExpression.create(checker, ctx.expr());
        IrBlock block = IrBlock.create(checker, ctx.block(), symbolTable);

        //TODO check that condition is boolean
        return new IrWhileStmt(condition, block);
    }
}