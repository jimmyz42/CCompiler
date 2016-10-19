package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;

import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import exceptions.TypeMismatchError;

public class IfStmt extends Statement {
    private Expression condition;
    private Block block;
    private Block elseBlock;

    public IfStmt(Expression condition, Block block, Block elseBlock) {
        this.condition = condition;
        this.block = block;
        this.elseBlock = elseBlock;
    }

    public static IfStmt create(DecafSemanticChecker checker, DecafParser.IfStmtContext ctx) {
        Expression condition = Expression.create(checker, ctx.expr());
        if (condition.getExpressionType() != ScalarType.BOOL) {
            throw new TypeMismatchError("If condition must be a bool", ctx.expr());
        }

        Block block = Block.create(checker, ctx.block(0), true);
        Block elseBlock;
        if (ctx.block().size() > 1) {
            elseBlock = Block.create(checker, ctx.block(1), true);
        } else {
            elseBlock = Block.createEmpty(checker, true);
        }

        return new IfStmt(condition, block, elseBlock);
    }
    
    @Override
    public void prettyPrint(PrintWriter pw, String prefix) {
    	pw.println(prefix + getClass().getSimpleName());
    	pw.println(prefix + "-condition:");
    	condition.prettyPrint(pw, prefix+"    ");
    	pw.println("\n" + prefix + "-IfBlock:");
        block.prettyPrint(pw, prefix + "    ");
    	pw.println(prefix + "-ElseBlock:");
        elseBlock.prettyPrint(pw, prefix + "    ");
        
    	pw.println(prefix + "end " + getClass().getSimpleName());
    }
}