package edu.mit.compilers.ir;

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
}
