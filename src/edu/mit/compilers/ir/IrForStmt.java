package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;
import exceptions.TypeMismatchException;

class IrForStmt extends IrStatement {
    private IrAssignStmt initializer;
    private IrExpression condition;
    private IrAssignStmt update;
    private IrBlock block;
    
    public IrForStmt(IrAssignStmt initializer, IrExpression condition, IrAssignStmt update, IrBlock block) {
        this.initializer = initializer;
        this.condition = condition;
        this.update = update;
        this.block = block;
    }

    public static IrForStmt create(DecafSemanticChecker checker, DecafParser.ForStmtContext ctx) {
        IrAssignStmt initializer = new IrAssignStmt(new IrIdLocation(new IrId(ctx.init_id.getText())), "=", IrExpression.create(checker, ctx.init_expr));
        
        IrExpression condition = IrExpression.create(checker, ctx.condition);
        if (condition.getExpressionType() != TypeScalar.BOOL) {
            throw new TypeMismatchException("For statement condition must be a bool");
        }
        
        IrAssignStmt update = new IrAssignStmt(new IrIdLocation(new IrId(ctx.update_id.getText())), ctx.update_op.getText(), IrExpression.create(checker, ctx.update_expr));
        IrBlock block = IrBlock.create(checker, ctx.block());

        return new IrForStmt(initializer, condition, update, block);
    }
}