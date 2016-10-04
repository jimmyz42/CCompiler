package edu.mit.compilers.ir;

import java.io.PrintWriter;

import edu.mit.compilers.PrettyPrintable;

public abstract class VariableDescriptor implements PrettyPrintable {
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
    
    @Override
    public void println(PrintWriter pw, String prefix) {
        pw.println(prefix + type + " " + name + ";");
    }
    
    @Override
    public String toString() {
        return type + " " + name;
    }
    
    // TODO: add stuff about where the variable is stored in memory
    //       that part will actually be different in Local and Global versions.
}
