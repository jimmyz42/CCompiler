package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;

import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import exceptions.TypeMismatchError;

public class AssignStmt extends Statement {
    private Location location;
    private Expression expression;
    private String assignOp;

    public AssignStmt(Location location, String assignOp, Expression expression) {
        this.location = location;
        this.assignOp = assignOp;
        this.expression = expression;
    }

    public static AssignStmt create(DecafSemanticChecker checker, DecafParser.AssignStmtContext ctx) {
        Location location = Location.create(checker, ctx.location());
        Expression expression = Expression.create(checker, ctx.expr());
        String assignOp = ctx.assign_op().getText();
        return create(location, assignOp, expression, ctx);
    }

    public static AssignStmt create(Location location, String assignOp, Expression expression, DecafParser.StatementContext ctx) {
        if (location.getExpressionType() != expression.getExpressionType()) {
            throw new TypeMismatchError("Two sides of an assignment should have the same type", ctx);
        }
        if (!(location.getExpressionType() instanceof ScalarType)) {
            throw new TypeMismatchError("Can only assign a scalar", ctx);
        }
        if (!assignOp.equals("=")) {
            if (location.getExpressionType() != ScalarType.INT) {
                throw new TypeMismatchError("Can only use += and -= on ints", ctx);
            }
            if (assignOp.equals("+=")) {
                expression = new AddOpExpr(new AddOp("+"), location, expression);
            } else if (assignOp.equals("-=")) {
                expression = new AddOpExpr(new AddOp("-"), location, expression);
            }
        }

        return new AssignStmt(location, assignOp, expression);
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