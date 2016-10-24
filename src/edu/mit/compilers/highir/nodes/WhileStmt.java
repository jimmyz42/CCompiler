package edu.mit.compilers.highir.nodes;

import java.util.ArrayList;

import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;

import edu.mit.compilers.cfg.CFGAble;
import edu.mit.compilers.cfg.CFGContext;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.cfg.components.BasicBlock;

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

    @Override
    public CFG generateCFG(CFGContext context) {
    	// TODO push CFG onto stack (pop at end of method)
    	// Need entry to be startCondition and exit to be escapeBlock
        CFG trueBranch = block.generateCFG();
        BasicBlock escapeBlock = BasicBlock.createEmpty();
        BasicBlock startCondition = condition.shortCircuit(trueBranch, escapeBlock);
        trueBranch.setNextBlock(startCondition);
        startCondition.addPreviousBlock(trueBranch.getEntryBlock());
        return new CFG(startCondition, escapeBlock);
    }
}




