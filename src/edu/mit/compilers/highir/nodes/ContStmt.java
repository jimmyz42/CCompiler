package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;

import edu.mit.compilers.cfg.CFGContext;
import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;

public class ContStmt extends Statement {
    public static ContStmt create(DecafSemanticChecker checker, DecafParser.ContinueStmtContext ctx) {
        return new ContStmt();
    }

    @Override
    public void cfgPrint(PrintWriter pw, String prefix) {
        pw.println(prefix + "continue");
    }

    @Override
    public CFG generateCFG(CFGContext context) {
    	BasicBlock block = BasicBlock.createEmpty();
    	BasicBlock loopEnter = context.currentLoopCFG().getEntryBlock();
    	block.setNextBlock(loopEnter);
    	loopEnter.addPreviousBlock(block);
    	return block;
    }
}