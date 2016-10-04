package edu.mit.compilers.ir;

public interface SymbolTable {
    VariableDescriptor addVariable(Type type, String name);
    VariableDescriptor getVariable(String name);
    // TODO addMethod, getMethod
}
