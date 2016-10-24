package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;

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
}