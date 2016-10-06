package edu.mit.compilers.ir;

import java.io.PrintWriter;

import edu.mit.compilers.grammar.DecafParser;

public class TypeScalar implements Type {
    public final static TypeScalar INT = new TypeScalar("int", 8);
    public final static TypeScalar BOOL = new TypeScalar("bool", 1);

    private String name;
    private long size;

    private TypeScalar(String name, long size) {
        this.name = name;
        this.size = size;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public String toString() {
        return name;
    }

    public static TypeScalar create(DecafSemanticChecker checker, DecafParser.TypeContext ctx) {
        if (ctx.TK_int() != null) return INT;
        if (ctx.TK_bool() != null) return BOOL;
        return null; // throw an exception?
    }
    
    @Override
    public void prettyPrint(PrintWriter pw, String prefix) {
        pw.print(prefix + toString());
    }
}
