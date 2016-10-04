package edu.mit.compilers.ir;

import java.util.HashMap;
import java.util.Map;

public class GlobalSymbolTable implements SymbolTable {
    private Map<String, GlobalVariableDescriptor> map = new HashMap<>();
    
    @Override
    public GlobalVariableDescriptor addVariable(Type type, String name) {
        GlobalVariableDescriptor descriptor = new GlobalVariableDescriptor(type, name);
        map.put(name, descriptor);
        return descriptor;
    }

    @Override
    public GlobalVariableDescriptor getVariable(String name) {
        return map.get(name);
    }
}
