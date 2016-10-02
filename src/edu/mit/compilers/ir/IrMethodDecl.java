package edu.mit.compilers.ir;

class IrMethodDecl extends Ir {
    private IrType type;
    private IrId id;
    private IrArgumentMethodDecl[] arguments;
    private IrBlock block;
}