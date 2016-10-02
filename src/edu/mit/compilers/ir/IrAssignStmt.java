package edu.mit.compilers.ir;

class IrAssignStmt extends IrStatement {
    private IrLocation location;
    private IrAssignOp assignOp;
    private IrExpression expression;
}