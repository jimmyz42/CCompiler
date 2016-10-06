package edu.mit.compilers.ir;

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
}
