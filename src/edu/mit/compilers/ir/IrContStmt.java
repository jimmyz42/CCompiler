package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;

class IrContStmt extends IrStatement {
    public static IrContStmt create(DecafSemanticChecker checker, DecafParser.ContinueStmtContext ctx, SymbolTable symbolTable) {
        return new IrContStmt();
    }
}