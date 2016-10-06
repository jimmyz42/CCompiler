package edu.mit.compilers.ir;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.v4.runtime.ParserRuleContext;

import exceptions.DuplicateIdentifierError;

public class GlobalSymbolTable extends SymbolTable {
    private Map<String, VariableDescriptor> variables = new HashMap<>();
    private Map<String, FunctionDescriptor> functions = new HashMap<>();
    
    @Override
    public VariableDescriptor addVariable(Type type, String name, ParserRuleContext ctx) {
        if (variables.containsKey(name)) {
            throw new DuplicateIdentifierError("Variable " + name + " already exists in this scope", ctx);
        }
        VariableDescriptor descriptor = new GlobalVariableDescriptor(type, name);
        variables.put(name, descriptor);
        return descriptor;
    }

    @Override
    public VariableDescriptor getVariable(String name) {
        return variables.get(name);
    }

    @Override
    public FunctionDescriptor addFunction(FunctionDescriptor function, ParserRuleContext ctx) {
        if (functions.containsKey(function.getName())) {
            throw new DuplicateIdentifierError("Function " + function.getName() + " already exists", ctx);
        }
        functions.put(function.getName(), function);
        return function;
    }

    @Override
    public FunctionDescriptor getFunction(String name) {
        return functions.get(name);
    }

    public Map<String, VariableDescriptor> getVariables() {
        return Collections.unmodifiableMap(variables);
    }

    public Map<String, FunctionDescriptor> getFunctions() {
        return Collections.unmodifiableMap(functions);
    }
}
