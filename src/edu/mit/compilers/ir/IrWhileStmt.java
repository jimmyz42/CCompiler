package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;
import exceptions.TypeMismatchError;

class IrWhileStmt extends IrStatement {
    private IrExpression condition;
    private IrBlock block;
    
    public IrWhileStmt(IrExpression condition, IrBlock block) {
        this.condition = condition;
        this.block = block;
    }

    public static IrWhileStmt create(DecafSemanticChecker checker, DecafParser.WhileStmtContext ctx) {
        IrExpression condition = IrExpression.create(checker, ctx.expr());
        if (condition.getExpressionType() != TypeScalar.BOOL) {
            throw new TypeMismatchError("While condition must be a bool", ctx.expr());
        }
        
        IrBlock block = IrBlock.create(checker, ctx.block());

        return new IrWhileStmt(condition, block);
    }
}