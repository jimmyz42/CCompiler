package edu.mit.compilers.highir.descriptor;

import java.io.PrintWriter;

import edu.mit.compilers.PrettyPrintable;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.highir.nodes.Type;

public abstract class VariableDescriptor extends Descriptor {
    public VariableDescriptor(String name, Type type) {
        super(name, type);
    }

    @Override
    public void prettyPrint(PrintWriter pw, String prefix) {
        pw.println(prefix + "VariableDescriptor: ");
        pw.print(prefix + "-type: ");
        getType().prettyPrint(pw, "");
        pw.println("\n" + prefix + "-name: " + getName());

    }

    @Override
    public String toString() {
        return getType() + " " + getName();
    }

    // TODO: add stuff about where the variable is stored in memory
    //       that part will actually be different in Local and Global versions.
}
