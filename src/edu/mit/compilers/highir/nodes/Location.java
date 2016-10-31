package edu.mit.compilers.highir.nodes;

import java.util.Arrays;

import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.grammar.DecafParser.LocationContext;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.highir.descriptor.VariableDescriptor;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.Register;
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
    public BasicBlock shortCircuit(CFG trueBranch, CFG falseBranch) {
    	EqOpExpr expr = new EqOpExpr(new EqOp("=="), this, new BoolLiteral(true));
        return expr.shortCircuit(trueBranch, falseBranch);
    }
    
    @Override
    public void generateAssembly(AssemblyContext ctx) {
    	variable.generateAssembly(ctx);
    }
    
    public Storage getLocation(AssemblyContext ctx) {
    	return ctx.getStackLocation(variable);
    }
    
    @Override
    public Register allocateRegister(AssemblyContext ctx) {
    	return ctx.allocateRegister(variable);
    }
    
    @Override
    public void deallocateRegister(AssemblyContext ctx) {
    	ctx.deallocateRegister(variable);
    }
}