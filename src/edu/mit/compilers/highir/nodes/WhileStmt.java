package edu.mit.compilers.highir.nodes;

import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import exceptions.TypeMismatchError;

public class WhileStmt extends Statement {
    private Expression condition;
    private Block block;

    public WhileStmt(Expression condition, Block block) {
        this.condition = condition;
        this.block = block;
    }

    public static WhileStmt create(DecafSemanticChecker checker, DecafParser.WhileStmtContext ctx) {
        Expression condition = Expression.create(checker, ctx.expr());
        if (condition.getExpressionType() != ScalarType.BOOL) {
            throw new TypeMismatchError("While condition must be a bool", ctx.expr());
        }

        Block block = Block.create(checker, ctx.block(), true);

        return new WhileStmt(condition, block);
    }
}