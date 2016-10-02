package edu.mit.compilers.ir;

class IrIfStmt extends IrStatement {
    private IrExpression condition;
    private IrBlock block;
    private IrBlock elseBlock;
}