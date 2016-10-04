package edu.mit.compilers.ir;

import java.util.HashMap;
import java.util.Map;

public class LocalSymbolTable extends SymbolTable {
    private SymbolTable parentTable;
    private Map<String, LocalVariableDescriptor> variables = new HashMap<>();
    
    public LocalSymbolTable(SymbolTable parentTable) {
        this.parentTable = parentTable;
    }
    
    @Override
    public LocalVariableDescriptor addVariable(Type type, String name) {
        LocalVariableDescriptor descriptor = new LocalVariableDescriptor(type, name);
        variables.put(name, descriptor);
        return descriptor;
    }

    @Override
    public VariableDescriptor getVariable(String name) {
        VariableDescriptor descriptor = variables.get(name);
        if (descriptor != null) {
            return descriptor;
        } else {
            return parentTable.getVariable(name);
        }
    }

    @Override
    public FunctionDescriptor addFunction(FunctionDescriptor function) {
        return parentTable.addFunction(function);
    }

    @Override
    public FunctionDescriptor getFunction(String name) {
        return parentTable.getFunction(name);
    }
}
