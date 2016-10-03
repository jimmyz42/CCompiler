package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;

class IrAssignStmt extends IrStatement {
    private IrLocation location;
    private IrAssignOp assignOp;
    private IrExpression expression;

    public IrAssignStmt(IrLocation location, IrAssignOp assignOp, IrExpression expression) {
        this.location = location;
        this.assignOp = assignOp;
        this.expression = expression;
    }

    public static IrAssignStmt create(DecafSemanticChecker checker, DecafParser.AssignStmtContext ctx) {
        IrLocation location = IrLocation.create(checker, ctx.location());
        IrAssignOp assignOp = IrAssignOp.create(checker, ctx.assign_op());
        IrExpression expression = IrExpression.create(checker, ctx.expr());

        //TODO check that location has been initialized
        //TODO different cases for each assignOp
        //TODO make sure the expression type is teh same as the location type
        return new IrAssignStmt(location, assignOp, expression);
    }
}