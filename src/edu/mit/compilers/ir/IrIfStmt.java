package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;

class IrIfStmt extends IrStatement {
    private IrExpression condition;
    private IrBlock block;
    private IrBlock elseBlock;

    public IrIfStmt(IrExpression condition, IrBlock block, IrBlock elseBlock) {
        this.condition = condition;
        this.block = block;
        this.elseBlock = elseBlock;
    }

    public static IrIfStmt create(DecafSemanticChecker checker, DecafParser.IfStmtContext ctx) {
        IrExpression condition = IrExpression.create(checker, ctx.expr());
        IrBlock block = IrBlock.create(checker, ctx.block(0));
        IrBlock elseBlock;
        if (ctx.block().size() > 1) {
            elseBlock = IrBlock.create(checker, ctx.block(1));
        } else {
            elseBlock = IrBlock.empty();
        }

        //TODO check that condition is boolean
        return new IrIfStmt(condition, block, elseBlock);
    }
}