package edu.mit.compilers.ir;

import java.io.PrintWriter;

public class ExternDescriptor extends FunctionDescriptor {
    public ExternDescriptor(String name) {
        super(name, TypeScalar.INT);
    }
    
    @Override
    public void println(PrintWriter pw, String prefix) {
        pw.println(prefix + "extern " + getName() + "();");
    }
}
