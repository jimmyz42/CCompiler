package edu.mit.compilers.ir;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.ParserRuleContext;

import exceptions.DuplicateIdentifierError;

public class LocalSymbolTable extends SymbolTable {
    private SymbolTable parentTable;
    private Map<String, VariableDescriptor> variables = new HashMap<>();

    public LocalSymbolTable(SymbolTable parentTable) {
        this.parentTable = parentTable;
    }

    protected void checkNamingConflicts(String name, ParserRuleContext ctx) {
        if (variables.containsKey(name)) {
            throw new DuplicateIdentifierError("Variable " + name + " already exists in this scope", ctx);
        }
        parentTable.checkNamingConflicts(name, ctx);
    }

    @Override
    public VariableDescriptor addVariable(Type type, String name, ParserRuleContext ctx) {
        if (variables.containsKey(name)) {
            throw new DuplicateIdentifierError("Variable " + name + " already exists in this scope", ctx);
        }
        VariableDescriptor descriptor = new LocalVariableDescriptor(type, name);
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
    public FunctionDescriptor addFunction(FunctionDescriptor function, ParserRuleContext ctx) {
        return parentTable.addFunction(function, ctx);
    }

    @Override
    public FunctionDescriptor getFunction(String name) {
        return parentTable.getFunction(name);
    }

    public Map<String, VariableDescriptor> getVariables() {
        return Collections.unmodifiableMap(variables);
    }
}
