package edu.mit.compilers.ir;

class IrWhileStmt extends IrStatement {
    private IrExpression condition;
    private IrBlock block;
}