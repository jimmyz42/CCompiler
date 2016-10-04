package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;

class IrAssignStmt extends IrStatement {
    private IrLocation location;
    private IrExpression expression;

    public IrAssignStmt(IrLocation location, String assignOp, IrExpression expression) {
        this.location = location;
        this.expression = expression;
        if (assignOp.equals("+=")) {
            expression = new IrAddOpExpr(new IrAddOp("+"), location, expression);
        } else if (assignOp.equals("-=")) {
            expression = new IrAddOpExpr(new IrAddOp("-"), location, expression);
        }
    }

    public static IrAssignStmt create(DecafSemanticChecker checker, DecafParser.AssignStmtContext ctx) {
        IrLocation location = IrLocation.create(checker, ctx.location());
        IrExpression expression = IrExpression.create(checker, ctx.expr());

        //TODO check that location has been declared
        //TODO make sure the expression type is the same as the location type
        return new IrAssignStmt(location, ctx.assign_op().getText(), expression);
    }
}