package edu.mit.compilers.highir.descriptor;

import java.io.PrintWriter;

import edu.mit.compilers.PrettyPrintable;
import edu.mit.compilers.cfg.CFGAble;
import edu.mit.compilers.highir.nodes.Type;


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
