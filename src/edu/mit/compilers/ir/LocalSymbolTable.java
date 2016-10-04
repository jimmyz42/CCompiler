package edu.mit.compilers.ir;

import java.util.HashMap;
import java.util.Map;

public class LocalSymbolTable implements SymbolTable {
    private SymbolTable parentTable;
    private Map<String, LocalVariableDescriptor> map = new HashMap<>();
    
    public LocalSymbolTable(SymbolTable parentTable) {
        this.parentTable = parentTable;
    }
    
    @Override
    public LocalVariableDescriptor addVariable(Type type, String name) {
        LocalVariableDescriptor descriptor = new LocalVariableDescriptor(type, name);
        map.put(name, descriptor);
        return descriptor;
    }

    @Override
    public VariableDescriptor getVariable(String name) {
        VariableDescriptor descriptor = map.get(name);
        if (descriptor != null) {
            return descriptor;
        } else {
            return parentTable.getVariable(name);
        }
    }
}
