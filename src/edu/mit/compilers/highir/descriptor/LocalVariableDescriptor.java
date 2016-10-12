package edu.mit.compilers.highir.descriptor;

import edu.mit.compilers.highir.nodes.Type;

public class LocalVariableDescriptor extends VariableDescriptor {
    public LocalVariableDescriptor(String name, Type type) {
        super(name, type);
    }
}
