package edu.mit.compilers.ir;

class IrMethodCall extends Ir {
    private MethodDescriptor method;
    private IrExpression[] expressions;
    private IrExternArg[] externArgs;
}