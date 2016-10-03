package edu.mit.compilers.ir;

import sun.org.mozilla.javascript.ast.Assignment;
import edu.mit.compilers.grammar.DecafParser;

class IrForStmt extends IrStatement {
    private IrAssignStmt initializer;
    private IrExpression condition;
    private IrAssignStmt update;
    private IrBlock block;
    
    public IrForStmt(IrAssignStmt initializer, IrExpression condition, IrAssignStmt update, IrBlock block) {
        super();
        this.initializer = initializer;
        this.condition = condition;
        this.update = update;
        this.block = block;
    }

    public static IrForStmt create(DecafSemanticChecker checker, DecafParser.ForStmtContext ctx) {
        IrAssignStmt initializer = new IrAssignStmt(new IrIdLocation(new IrId(ctx.init_id.getText())), "=", IrExpression.create(checker, ctx.init_expr));
        IrExpression condition = IrExpression.create(checker, ctx.condition);
        IrAssignStmt update = new IrAssignStmt(new IrIdLocation(new IrId(ctx.update_id.getText())), ctx.update_op.getText(), IrExpression.create(checker, ctx.update_expr));
        IrBlock block = IrBlock.create(checker, ctx.block());

        //TODO check that condition is boolean
        return new IrForStmt(initializer, condition, update, block);
    }
}