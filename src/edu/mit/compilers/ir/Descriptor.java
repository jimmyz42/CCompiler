package edu.mit.compilers.ir;

import java.io.PrintWriter;

import edu.mit.compilers.PrettyPrintable;


public abstract class Descriptor implements PrettyPrintable {
    private final String name;
    private final Type returnType;

    public Descriptor(String name, Type returnType) {
        this.name = name;
        this.returnType = returnType;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return returnType;
    }
}
