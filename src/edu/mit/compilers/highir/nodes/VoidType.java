package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;

public class VoidType implements Type {
    public static VoidType VOID = new VoidType();

    private VoidType() {
    }

    @Override
    public long getLength() {
        return 0;
    }

    @Override
    public long getSize() {
        return 0;
    }

    @Override
    public String toString() {
        return "void";
    }
    
    @Override
    public void prettyPrint(PrintWriter pw, String prefix) {
        pw.print(prefix + toString());
    }
}
