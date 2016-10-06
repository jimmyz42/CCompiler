package edu.mit.compilers.ir;

<<<<<<< HEAD
import java.io.PrintWriter;

import exceptions.BadArraySizeError;

=======
>>>>>>> fe377eb91a926c0bbced958bae835c4cdb9698d0
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
