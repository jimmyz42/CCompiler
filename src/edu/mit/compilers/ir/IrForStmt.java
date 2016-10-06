package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;
import exceptions.TypeMismatchError;

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
        SymbolTable symbolTable = checker.currentSymbolTable();

        IrIdLocation iterator = new IrIdLocation(symbolTable.getVariable(ctx.init_id.getText()));
        if (iterator.getExpressionType() != TypeScalar.INT) {
            throw new TypeMismatchError("For loop iterator must be an int", ctx);
        }
        IrAssignStmt initializer = IrAssignStmt.create(
                iterator,
                "=",
                IrExpression.create(checker, ctx.init_expr),
                ctx);

        IrExpression condition = IrExpression.create(checker, ctx.condition);
        if (condition.getExpressionType() != TypeScalar.BOOL) {
            throw new TypeMismatchError("For loop condition must be a bool", ctx.condition);
        }

        IrAssignStmt update = IrAssignStmt.create(
                new IrIdLocation(symbolTable.getVariable(ctx.update_id.getText())),
                ctx.update_op.getText(),
                IrExpression.create(checker, ctx.update_expr),
                ctx);
        IrBlock block = IrBlock.create(checker, ctx.block(), true);

        return new IrForStmt(initializer, condition, update, block);
    }
}