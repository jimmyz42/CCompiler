package edu.mit.compilers.highir.nodes;

import java.util.ArrayList;

import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;

import edu.mit.compilers.cfg.CFGAble;
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
    public CFG generateCFG() {
        ArrayList<BasicBlock> entryBranches = new ArrayList<>();
        CFG trueBranch = block.generateCFG();
        BasicBlock escapeBlock = BasicBlock.create();
        entryBranches.add(trueBranch.getEntryBlock());
        entryBranches.add(escapeBlock);

        CFG conditionCFG = condition.generateCFG();
        conditionCFG.setNextBlocks(entryBranches);

        trueBranch.setPreviousBlock(conditionCFG.getExitBlock());
        trueBranch.setNextBlock(conditionCFG.getEntryBlock());

        ArrayList<BasicBlock> exitBranches = new ArrayList<>();
        exitBranches.add(trueBranch.getExitBlock());
        exitBranches.add(conditionCFG.getExitBlock());
        escapeBlock.setPreviousBlocks(exitBranches);

        return new CFG(conditionCFG.getEntryBlock(), escapeBlock);
    }
}