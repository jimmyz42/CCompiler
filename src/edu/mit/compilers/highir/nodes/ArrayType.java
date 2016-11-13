package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;

public class ArrayType implements Type {
    private final long length;
    private final ScalarType elementType;

    public ArrayType(long length, ScalarType elementType) {
        this.length = length;
        this.elementType = elementType;
    }

    @Override
    public long getLength() {
        return length;
    }

    public ScalarType getElementType() {
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
