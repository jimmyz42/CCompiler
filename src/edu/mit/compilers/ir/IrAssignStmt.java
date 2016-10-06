package edu.mit.compilers.ir;

import java.io.PrintWriter;

import edu.mit.compilers.grammar.DecafParser;
import exceptions.TypeMismatchError;

class IrAssignStmt extends IrStatement {
    private IrLocation location;
    private IrExpression expression;
    private String assignOp;

    public IrAssignStmt(IrLocation location, String assignOp, IrExpression expression) {
        this.location = location;
        this.assignOp = assignOp;
        this.expression = expression;
    }

    public static IrAssignStmt create(DecafSemanticChecker checker, DecafParser.AssignStmtContext ctx) {
        IrLocation location = IrLocation.create(checker, ctx.location());
        IrExpression expression = IrExpression.create(checker, ctx.expr());
        String assignOp = ctx.assign_op().getText();
        return create(location, assignOp, expression, ctx);
    }

    public static IrAssignStmt create(IrLocation location, String assignOp, IrExpression expression, DecafParser.StatementContext ctx) {
        if (location.getExpressionType() != expression.getExpressionType()) {
            throw new TypeMismatchError("Two sides of an assignment should have the same type", ctx);
        }
        if (!(location.getExpressionType() instanceof TypeScalar)) {
            throw new TypeMismatchError("Can only assign a scalar", ctx);
        }
        if (!assignOp.equals("=")) {
            if (location.getExpressionType() != TypeScalar.INT) {
                throw new TypeMismatchError("Can only use += and -= on ints", ctx);
            }
            if (assignOp.equals("+=")) {
                expression = new IrAddOpExpr(new IrAddOp("+"), location, expression);
            } else if (assignOp.equals("-=")) {
                expression = new IrAddOpExpr(new IrAddOp("-"), location, expression);
            }
        }

        return new IrAssignStmt(location, assignOp, expression);
    }

    @Override
    public void prettyPrint(PrintWriter pw, String prefix) {
        super.prettyPrint(pw, prefix);
        pw.println(prefix + "-location:");
        location.prettyPrint(pw, prefix + "    ");
        pw.println(prefix+ "-operator: " + assignOp);
        pw.println(prefix + "-expression:");
        expression.prettyPrint(pw, prefix + "    ");
    }
}