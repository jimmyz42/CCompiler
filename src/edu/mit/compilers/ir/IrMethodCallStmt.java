package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser.MethodCallStmtContext;

public class IrMethodCallStmt extends IrStatement {
    private final IrMethodCallExpr methodCall;
    public IrMethodCallStmt(IrMethodCallExpr methodCall) {
        this.methodCall = methodCall;
    }

    public static IrMethodCallStmt create(DecafSemanticChecker checker, MethodCallStmtContext ctx) {
        return new IrMethodCallStmt(IrMethodCallExpr.create(checker, ctx.method_call()));
    }
}
