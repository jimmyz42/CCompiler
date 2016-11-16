package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;

import edu.mit.compilers.cfg.CFGContext;
import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;

public class BreakStmt extends Statement {
    public static BreakStmt create(DecafSemanticChecker checker, DecafParser.BreakStmtContext ctx) {
        return new BreakStmt();
    }

    @Override
    public void cfgPrint(PrintWriter pw, String prefix) {
        pw.println(prefix + "break");
    }
    
    @Override
    public CFG generateCFG(CFGContext context) {
    	BasicBlock block = BasicBlock.createEmpty("break");
    	BasicBlock loopExit = context.currentLoopCFG().getExitBlock();
    	block.setNextBlock(loopExit);
    	loopExit.addPreviousBlock(block);
    	return block;
    }
}