package edu.mit.compilers.highir.descriptor;

import edu.mit.compilers.highir.nodes.Type;

public class GlobalVariableDescriptor extends VariableDescriptor {
    public GlobalVariableDescriptor(String name, Type type) {
        super(name, type);
    }
}
