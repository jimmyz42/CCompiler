package edu.mit.compilers.ir;

public abstract class VariableDescriptor {
    private final Type type;
    private final String name;
    
    public VariableDescriptor(Type type, String name) {
        this.type = type;
        this.name = name;
    }
    
    public Type getType() {
        return type;
    }
    public String getName() {
        return name;
    }
    // TODO: add stuff about where the variable is stored in memory
    //       that part will actually be differt in Local and Global versions.
}
