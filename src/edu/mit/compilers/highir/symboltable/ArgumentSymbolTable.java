package edu.mit.compilers.highir.symboltable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.ParserRuleContext;
import edu.mit.compilers.highir.descriptor.*;
import edu.mit.compilers.highir.nodes.Type;

import exceptions.DuplicateIdentifierError;

public class ArgumentSymbolTable extends SymbolTable {
    private SymbolTable parentTable;
    private Map<String, VariableDescriptor> variables = new HashMap<>();

    public ArgumentSymbolTable(SymbolTable parentTable) {
        this.parentTable = parentTable;
    }

    protected void checkNamingConflicts(String name, ParserRuleContext ctx) {
        if (variables.containsKey(name)) {
            throw new DuplicateIdentifierError("Variable " + name + " already exists in this scope", ctx);
        }
    }

    @Override
    public VariableDescriptor addVariable(Type type, String name, ParserRuleContext ctx) {
        checkNamingConflicts(name, ctx);
        VariableDescriptor descriptor = new LocalVariableDescriptor(name, type);
        variables.put(name, descriptor);
        return descriptor;
    }

    @Override
    public VariableDescriptor getVariable(String name, ParserRuleContext ctx) {
        VariableDescriptor descriptor = variables.get(name);
        if (descriptor != null) {
            return descriptor;
        } else {
            return parentTable.getVariable(name, ctx);
        }
    }

    //TODO remove this function
    @Override
    public FunctionDescriptor addFunction(FunctionDescriptor function, ParserRuleContext ctx) {
        return parentTable.addFunction(function, ctx);
    }

    @Override
    public FunctionDescriptor getFunction(String name, ParserRuleContext ctx) {
        return parentTable.getFunction(name, ctx);
    }

    public Map<String, VariableDescriptor> getVariables() {
        return Collections.unmodifiableMap(variables);
    }
}
