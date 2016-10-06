package edu.mit.compilers.ir;

import java.io.PrintWriter;

public class TypeVoid implements Type {
    public static TypeVoid VOID = new TypeVoid();

    private TypeVoid() {
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
