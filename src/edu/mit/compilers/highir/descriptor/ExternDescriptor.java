package edu.mit.compilers.highir.descriptor;

import java.io.PrintWriter;
import edu.mit.compilers.highir.nodes.ScalarType;

public class ExternDescriptor extends FunctionDescriptor {
    public ExternDescriptor(String name) {
        super(name, ScalarType.INT);
    }

    @Override
    public void prettyPrint(PrintWriter pw, String prefix) {
        pw.println(prefix + "extern " + getName());
    }
}
