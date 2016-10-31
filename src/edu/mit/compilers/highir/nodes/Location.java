package edu.mit.compilers.highir.nodes;

import edu.mit.compilers.grammar.DecafParser.LocationContext;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.highir.descriptor.VariableDescriptor;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.Memory;
import edu.mit.compilers.lowir.Storage;

abstract public class Location extends Expression {
    private VariableDescriptor variable;

    Location(VariableDescriptor variable) {
        this.variable = variable;
    }

    public static Location create(DecafSemanticChecker checker, LocationContext ctx) {
        return (Location) checker.visit(ctx);
    }

    public VariableDescriptor getVariable() {
        return variable;
    }

    @Override
    public Type getExpressionType() {
        return variable.getType();
    }
    
    @Override
    public void generateAssembly(AssemblyContext ctx) {
    	variable.generateAssembly(ctx);
    }
    
    public Storage getLocation(AssemblyContext ctx) {
    	return ctx.getStackLocation(variable);
    }
}