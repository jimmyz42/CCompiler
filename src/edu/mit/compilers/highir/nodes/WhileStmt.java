package edu.mit.compilers.highir.nodes;

import edu.mit.compilers.cfg.CFGContext;
import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.cfg.components.CFG;
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
        if (condition.getType() != ScalarType.BOOL) {
            throw new TypeMismatchError("While condition must be a bool", ctx.expr());
        }

        Block block = Block.create(checker, ctx.block(), true);

        return new WhileStmt(condition, block);
    }

    @Override
    public CFG generateCFG(CFGContext context) {
    	// startBlock is NOP for allowing continue; statements
    	// because startCondition has to be evaluated after block.generateCFG()
    	BasicBlock startBlock = BasicBlock.createEmpty("while start");
        BasicBlock escapeBlock = BasicBlock.createEmpty("while escape");
        
    	// Note: Need to push this BEFORE calling block.generateCFG()
    	// so that any break/continue statements are taken care of
    	context.pushLoopCFG(new CFG(startBlock, escapeBlock));

        CFG trueBranch = block.generateCFG(context);
        trueBranch.getEntryBlock().setDescription("while start");
        BasicBlock startCondition = condition.shortCircuit(trueBranch, escapeBlock);
        startBlock.setNextBlock(startCondition);
        startCondition.addPreviousBlocks(startBlock.getPreviousBlocks());
        trueBranch.setNextBlock(startCondition);
        startCondition.addPreviousBlock(trueBranch.getEntryBlock());

        context.popLoopCFG();

        return new CFG(startCondition, escapeBlock);
    }
}




