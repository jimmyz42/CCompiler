package edu.mit.compilers.ir;

class IrForStmt extends IrStatement {
    private IrInitForStmt initializer;
    private IrExpression  condition;
    private IrUpdateForStmt update;
    private IrBlock block;
}