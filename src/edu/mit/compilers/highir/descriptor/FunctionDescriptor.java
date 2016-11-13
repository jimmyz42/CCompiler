package edu.mit.compilers.highir.descriptor;

import java.io.PrintWriter;

import edu.mit.compilers.highir.nodes.Type;

public abstract class FunctionDescriptor extends Descriptor {
    public FunctionDescriptor(String name, Type returnType) {
        super(name, returnType);
    }

    @Override
    public void prettyPrint(PrintWriter pw, String prefix) {
    	pw.println("THE PRETTY PRINT OF THE FUNCTION");
        pw.println(prefix + getClass().getSimpleName());
        pw.println(prefix + "-name: " + getName());
        pw.println(prefix + "-returnType: ");
        getType().prettyPrint(pw,"");
    }

    abstract public String getExpressionName();
}
