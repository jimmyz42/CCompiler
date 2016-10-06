package edu.mit.compilers.ir;

import java.io.PrintWriter;

import exceptions.BadArraySizeError;

public class TypeArray implements Type {
    private final long length;
    private final Type elementType;

    public TypeArray(long length, Type elementType) {
        this.length = length;
        this.elementType = elementType;
    }

    public long getLength() {
        return length;
    }

    public Type getElementType() {
        return elementType;
    }

    @Override
    public long getSize() {
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
