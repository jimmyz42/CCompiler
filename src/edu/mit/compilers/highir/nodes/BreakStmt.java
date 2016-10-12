package edu.mit.compilers.highir.nodes;

import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;

public class BreakStmt extends Statement {
    public static BreakStmt create(DecafSemanticChecker checker, DecafParser.BreakStmtContext ctx) {
        return new BreakStmt();
    }
}