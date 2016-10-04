package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;

class IrBreakStmt extends IrStatement {
    public static IrBreakStmt create(DecafSemanticChecker checker, DecafParser.BreakStmtContext ctx) {
        return new IrBreakStmt();
    }
}