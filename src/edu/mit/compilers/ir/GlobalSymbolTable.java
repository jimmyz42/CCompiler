package edu.mit.compilers.ir;

import java.util.HashMap;
import java.util.Map;

public class GlobalSymbolTable extends SymbolTable {
    private Map<String, GlobalVariableDescriptor> variables = new HashMap<>();
    private Map<String, FunctionDescriptor> functions = new HashMap<>();
    
    @Override
    public GlobalVariableDescriptor addVariable(Type type, String name) {
        GlobalVariableDescriptor descriptor = new GlobalVariableDescriptor(type, name);
        variables.put(name, descriptor);
        return descriptor;
    }

    @Override
    public GlobalVariableDescriptor getVariable(String name) {
        return variables.get(name);
    }

    @Override
    public FunctionDescriptor addFunction(FunctionDescriptor function) {
        functions.put(function.getName(), function);
        return function;
    }

    @Override
    public FunctionDescriptor getFunction(String name) {
        return functions.get(name);
    }
}
