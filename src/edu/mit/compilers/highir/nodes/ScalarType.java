package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;

import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;

public class ScalarType implements Type {
    public final static ScalarType INT = new ScalarType("int", 8);
    public final static ScalarType BOOL = new ScalarType("bool", 1);

    private String name;
    private long size;

    private ScalarType(String name, long size) {
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

    public static ScalarType create(DecafSemanticChecker checker, DecafParser.TypeContext ctx) {
        if (ctx.TK_int() != null) return INT;
        if (ctx.TK_bool() != null) return BOOL;
        return null; // throw an exception?
    }
    
    @Override
    public void prettyPrint(PrintWriter pw, String prefix) {
        pw.print(prefix + toString());
    }
}
