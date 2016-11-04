package edu.mit.compilers.highir.symboltable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.PrintWriter;

import org.antlr.v4.runtime.ParserRuleContext;
import edu.mit.compilers.highir.descriptor.*;
import edu.mit.compilers.highir.nodes.Ir;
import edu.mit.compilers.highir.nodes.Type;

import edu.mit.compilers.cfg.CFGAble;

import exceptions.DuplicateIdentifierError;
import exceptions.TypeMismatchError;

public class GlobalSymbolTable extends SymbolTable {
    private Map<String, Descriptor> descriptors = new HashMap<>();

    protected void checkNamingConflicts(String name, ParserRuleContext ctx) {
        if (descriptors.containsKey(name)) {
            Descriptor descriptor =  descriptors.get(name);
            if(descriptor instanceof VariableDescriptor) {
                throw new DuplicateIdentifierError("Variable " + name + " already exists in this scope", ctx);
            }
            if(descriptor instanceof FunctionDescriptor) {
                throw new DuplicateIdentifierError("Function " + name + " already exists", ctx);
            }
        }
    }

    @Override
    public VariableDescriptor addVariable(Type type, String name, ParserRuleContext ctx) {
        checkNamingConflicts(name, ctx);
        VariableDescriptor descriptor = VariableDescriptor.create(name, type, true);
        descriptors.put(name, descriptor);
        return descriptor;
    }

    @Override
    public VariableDescriptor getVariable(String name, ParserRuleContext ctx) {
        try {
            return (VariableDescriptor) descriptors.get(name);
        } catch (ClassCastException e) {
            throw new TypeMismatchError("Expected a variable but " + name + " is not one", ctx);
        }
    }

    @Override
    public FunctionDescriptor addFunction(FunctionDescriptor function, ParserRuleContext ctx) {
        checkNamingConflicts(function.getName(), ctx);
        descriptors.put(function.getName(), function);
        return function;
    }

    @Override
    public FunctionDescriptor getFunction(String name, ParserRuleContext ctx) {
        try {
            return (FunctionDescriptor) descriptors.get(name);
        } catch (ClassCastException e) {
            throw new TypeMismatchError("Expected a function but " + name + " is not one", ctx);
        }
    }

    public Map<String, Descriptor> getDescriptors() {
        return Collections.unmodifiableMap(descriptors);
    }
}
