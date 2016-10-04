package edu.mit.compilers.ir;

import edu.mit.compilers.PrettyPrintable;


public abstract class FunctionDescriptor implements PrettyPrintable {
    private final String name;
    private final Type returnType;
    
    public FunctionDescriptor(String name, Type returnType) {
        this.name = name;
        this.returnType = returnType;
    }
    
    public String getName() {
        return name;
    }
    
    public Type getReturnType() {
        return returnType;
    }
}
