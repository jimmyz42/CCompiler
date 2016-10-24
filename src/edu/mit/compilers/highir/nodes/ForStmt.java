package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;

import java.util.ArrayList;

import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.highir.descriptor.VariableDescriptor;
import edu.mit.compilers.highir.symboltable.SymbolTable;

import edu.mit.compilers.cfg.CFGAble;
import edu.mit.compilers.cfg.CFGContext;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.cfg.components.BasicBlock;

import exceptions.TypeMismatchError;

public class ForStmt extends Statement {
    private AssignStmt initializer;
    private Expression condition;
    private AssignStmt update;
    private Block block;

    public ForStmt(AssignStmt initializer, Expression condition, AssignStmt update, Block block) {
        this.initializer = initializer;
        this.condition = condition;
        this.update = update;
        this.block = block;
    }

    public static ForStmt create(DecafSemanticChecker checker, DecafParser.ForStmtContext ctx) {
        SymbolTable symbolTable = checker.currentSymbolTable();

        IdLocation iterator = new IdLocation(symbolTable.getVariable(ctx.init_id.getText(), ctx));
        if (iterator.getExpressionType() != ScalarType.INT) {
            throw new TypeMismatchError("For loop iterator must be an int", ctx);
        }
        AssignStmt initializer = AssignStmt.create(
                iterator,
                "=",
                Expression.create(checker, ctx.init_expr),
                ctx);

        Expression condition = Expression.create(checker, ctx.condition);
        if (condition.getExpressionType() != ScalarType.BOOL) {
            throw new TypeMismatchError("For loop condition must be a bool", ctx.condition);
        }

        AssignStmt update = AssignStmt.create(
                new IdLocation(symbolTable.getVariable(ctx.update_id.getText(), ctx)),
                ctx.update_op.getText(),
                Expression.create(checker, ctx.update_expr),
                ctx);
        Block block = Block.create(checker, ctx.block(), true);

        return new ForStmt(initializer, condition, update, block);
    }

    @Override
    public void prettyPrint(PrintWriter pw, String prefix) {
    	super.prettyPrint(pw, prefix);
    	pw.println(prefix + "-initializer:");
        initializer.prettyPrint(pw, prefix + "    ");
        pw.println(prefix + "-condition:");
        condition.prettyPrint(pw, prefix+"    ");
        pw.println(prefix + "-update");
        update.prettyPrint(pw, prefix +  "    ");
        pw.println(prefix + "-forBlock:");
        block.prettyPrint(pw, prefix + "    ");
    }
    
    @Override
    public CFG generateCFG() {
    	// TODO push CFG onto stack (pop at end of method)
    	// Need entry to be updateCFG.entryBlock() and exit to be escapeBlock
    	CFG initializerCFG = initializer.generateCFG();   
    	CFG trueBranch = block.generateCFG();
    	CFG updateCFG = update.generateCFG();
    	BasicBlock escapeBlock = BasicBlock.createEmpty();
    	BasicBlock startCondition = condition.shortCircuit(trueBranch, escapeBlock);
      
    	initializerCFG.setNextBlock(startCondition);
    	startCondition.addPreviousBlock(initializerCFG.getExitBlock());  
      
    	trueBranch.setNextBlock(updateCFG.getEntryBlock());
    	updateCFG.setPreviousBlock(trueBranch.getExitBlock());
    	
    	updateCFG.setNextBlock(startCondition);
    	startCondition.addPreviousBlock(updateCFG.getExitBlock());
    	
    	return new CFG(initializerCFG.getEntryBlock(), escapeBlock);
    }
}