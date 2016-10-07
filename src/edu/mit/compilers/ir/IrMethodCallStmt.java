package edu.mit.compilers.ir;

import java.io.PrintWriter;

import edu.mit.compilers.grammar.DecafParser.MethodCallStmtContext;

public class IrMethodCallStmt extends IrStatement {
    private final IrMethodCallExpr methodCall;
    public IrMethodCallStmt(IrMethodCallExpr methodCall) {
        this.methodCall = methodCall;
    }

    public static IrMethodCallStmt create(DecafSemanticChecker checker, MethodCallStmtContext ctx) {
        return new IrMethodCallStmt(IrMethodCallExpr.create(checker, ctx.method_call()));
    }
    
    @Override
    public void prettyPrint(PrintWriter pw, String prefix) {
        super.prettyPrint(pw, prefix);
        methodCall.prettyPrint(pw, prefix+"    ");
    }
}
