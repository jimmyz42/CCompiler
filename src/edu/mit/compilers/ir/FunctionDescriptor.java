package edu.mit.compilers.ir;

import java.io.PrintWriter;

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
    
    @Override
    public void prettyPrint(PrintWriter pw, String prefix) {
    	pw.println("THE PRETTY PRINT OF THE FUNCTION");
        pw.println(prefix + "// " + getClass().getSimpleName());
        pw.println(prefix + "-name: " + name);
        pw.println(prefix + "-returnType: ");
        returnType.prettyPrint(pw,"");
    }
    
}
