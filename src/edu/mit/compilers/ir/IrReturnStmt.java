package edu.mit.compilers.ir;

import java.io.PrintWriter;

import edu.mit.compilers.grammar.DecafParser;
import exceptions.TypeMismatchError;

class IrReturnStmt extends IrStatement {
    private final IrExpression expression;

    public IrReturnStmt(IrExpression expression) {
        this.expression = expression;
    }

    public static IrReturnStmt create(DecafSemanticChecker checker, DecafParser.ReturnStmtContext ctx) {
        IrExpression expression = IrExpression.create(checker, ctx.expr());
        Type methodType = checker.currentMethodDescriptor().getReturnType();
        if(expression.getExpressionType() != methodType) {
            throw new TypeMismatchError("Return type " + expression.getExpressionType() + " does not match the expected type " + methodType, ctx);
        }
        return new IrReturnStmt(expression);
    }

    @Override
    public void prettyPrint(PrintWriter pw, String prefix) {
        super.prettyPrint(pw, prefix);
        pw.println(prefix + "-return:");
        expression.prettyPrint(pw, prefix + "    ");
    }
}