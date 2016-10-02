package edu.mit.compilers.ir;

class IrMethodCall extends Ir {
    private IrMethodName name;
    private IrExpression[] expressions;
    private IrExternArg[] externArgs;
}