package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;

import edu.mit.compilers.cfg.CFGContext;
import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.grammar.DecafParser.MethodCallStmtContext;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.lowir.AssemblyContext;

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
    public CFG generateCFG(CFGContext context) {
        return BasicBlock.create(methodCall);
    }
}
