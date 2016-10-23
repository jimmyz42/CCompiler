package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;

import java.util.ArrayList;

import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;

import edu.mit.compilers.cfg.CFGAble;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.cfg.components.BasicBlock;

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

    @Override
    public CFG generateCFG() {
        ArrayList<BasicBlock> entryBranches = new ArrayList<>();
        CFG trueBranch = block.generateCFG();
        CFG falseBranch = elseBlock.generateCFG();
        entryBranches.add(trueBranch.getEntryBlock());
        entryBranches.add(falseBranch.getEntryBlock());

        CFG conditionCFG = condition.generateCFG();
        conditionCFG.setNextBlocks(entryBranches);
        trueBranch.setPreviousBlock(conditionCFG.getExitBlock());
        falseBranch.setPreviousBlock(conditionCFG.getExitBlock());

        BasicBlock escapeBlock = BasicBlock.create();
        trueBranch.setNextBlock(escapeBlock);
        falseBranch.setNextBlock(escapeBlock);

        ArrayList<BasicBlock> exitBranches = new ArrayList<>();
        exitBranches.add(trueBranch.getExitBlock());
        exitBranches.add(falseBranch.getExitBlock());
        escapeBlock.setPreviousBlocks(exitBranches);

        return new CFG(conditionCFG.getEntryBlock(), escapeBlock);
    }
}