package edu.mit.compilers.ir;

public class ExternDescriptor extends FunctionDescriptor {
    public ExternDescriptor(String name) {
        super(name, TypeScalar.INT);
    }
}
