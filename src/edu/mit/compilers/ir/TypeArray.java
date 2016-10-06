package edu.mit.compilers.ir;

import java.io.PrintWriter;

import exceptions.BadArraySizeError;

public class TypeArray implements Type {
    private final int length;
    private final Type elementType;
    
    public TypeArray(int length, Type elementType) {
        this.length = length;
        this.elementType = elementType;
    }

    public int getLength() {
        return length;
    }

    public Type getElementType() {
        return elementType;
    }

    @Override
    public int getSize() {
        return length * elementType.getSize();
    }

    @Override
    public String toString() {
        return elementType.toString() + "[" + length + "]";
    }

    @Override
    public void prettyPrint(PrintWriter pw, String prefix) {
        pw.print(prefix + toString());
    }
}
