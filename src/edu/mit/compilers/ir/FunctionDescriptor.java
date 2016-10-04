package edu.mit.compilers.ir;


public abstract class FunctionDescriptor {
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
