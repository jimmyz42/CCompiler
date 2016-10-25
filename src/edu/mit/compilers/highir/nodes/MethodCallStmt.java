package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;

import edu.mit.compilers.grammar.DecafParser.MethodCallStmtContext;
import edu.mit.compilers.highir.DecafSemanticChecker;

public class MethodCallStmt extends Statement {
    private final MethodCallExpr methodCall;
    public MethodCallStmt(MethodCallExpr methodCall) {
        this.methodCall = methodCall;
    }

    public static MethodCallStmt create(DecafSemanticChecker checker, MethodCallStmtContext ctx) {
        return new MethodCallStmt(MethodCallExpr.create(checker, ctx.method_call()));
    }

    @Override
    public void prettyPrint(PrintWriter pw, String prefix) {
        super.prettyPrint(pw, prefix);
        methodCall.prettyPrint(pw, prefix+"    ");
    }

    @Override
    public void cfgPrint(PrintWriter pw, String prefix) {
        methodCall.cfgPrint(pw, prefix);
        pw.println();
    }
}
