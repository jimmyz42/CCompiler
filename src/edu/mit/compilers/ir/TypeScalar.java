package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;
import exceptions.TypeMismatchError;

public class TypeScalar implements Type {
    public final static TypeScalar INT = new TypeScalar("int", 8);
    public final static TypeScalar BOOL = new TypeScalar("bool", 1);
    
    private String name;
    private int size;

    private TypeScalar(String name, int size) {
        this.name = name;
        this.size = size;
    }
    
    @Override
    public int getSize() {
        return size;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    public static TypeScalar create(DecafSemanticChecker checker, DecafParser.TypeContext ctx) {
        if (!ctx.TK_int().getText().isEmpty()) return INT;
        if (!ctx.TK_bool().getText().isEmpty()) return BOOL;
        return null; // throw an exception?
    }
}
