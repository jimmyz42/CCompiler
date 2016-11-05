package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;

import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.highir.descriptor.ArrayVariableDescriptor;
import edu.mit.compilers.highir.descriptor.VariableDescriptor;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.ImmediateValue;
import edu.mit.compilers.lowir.Memory;
import edu.mit.compilers.lowir.Register;
import edu.mit.compilers.lowir.Storage;
import edu.mit.compilers.lowir.instructions.Cmp;
import edu.mit.compilers.lowir.instructions.Jae;
import edu.mit.compilers.lowir.instructions.Mov;
import edu.mit.compilers.lowir.instructions.Sub;
import edu.mit.compilers.lowir.instructions.Xor;
import exceptions.TypeMismatchError;
import exceptions.UndeclaredIdentifierError;

public class ArrayLocation extends Location {
    private Expression index;

    public ArrayLocation(ArrayVariableDescriptor variable, Expression index) {
        super(variable);
        this.index = index;
    }

    public static ArrayLocation create(DecafSemanticChecker checker, DecafParser.ArrayLocationContext ctx) {
        String varName = ctx.ID().getText();
        VariableDescriptor variable = checker.currentSymbolTable().getVariable(varName, ctx);
        Expression index = Expression.create(checker, ctx.expr());

        if (variable == null) {
            throw new UndeclaredIdentifierError("Array is not declared", ctx);
        }
        if (!(variable.getType() instanceof ArrayType)) {
            throw new TypeMismatchError("Can only index variables", ctx);
        }
        if (index.getExpressionType() != ScalarType.INT) {
            throw new TypeMismatchError("Array index must be an int", ctx.expr());
        }

        return new ArrayLocation((ArrayVariableDescriptor) variable, index);
    }

    @Override
    public Type getExpressionType() {
        return ((ArrayType) getVariable().getType()).getElementType();
    }

    @Override
    public ArrayVariableDescriptor getVariable() {
    	return (ArrayVariableDescriptor) super.getVariable();
    }

    @Override
    public void prettyPrint(PrintWriter pw, String prefix) {
        super.prettyPrint(pw, prefix);
        pw.println(prefix + getVariable().getName() + "[");
        index.prettyPrint(pw, prefix + "  ");
        pw.println(prefix + "] ");
    }

    @Override
    public void cfgPrint(PrintWriter pw, String prefix) {
        pw.print(prefix + getVariable().getName() + "[");
        index.cfgPrint(pw,"");
        pw.print("]");
    }

    @Override
    public void generateAssembly(AssemblyContext ctx) {
    	index.generateAssembly(ctx);
    }

	@Override
	public long getNumStackAllocations() {
		return index.getNumStackAllocations();
	}

    public Storage getLocation(AssemblyContext ctx) {
    	ctx.addInstruction(Mov.create(index.getLocation(ctx), Register.create("%rax")));
    	ctx.addInstruction(Cmp.create(ImmediateValue.create(variable.getType().getLength()),Register.create("%rax")));
    	ctx.addInstruction(Jae.create(Memory.create("array_index_error")));
    	ctx.addInstruction(Xor.create(Register.create("%rdx"), Register.create("%rdx")));
        ctx.addInstruction(Sub.create(index.getLocation(ctx), Register.create("%rdx")));
    	return getVariable().getLocation(ctx, Register.create("%rdx"));
    }

    @Override
    public Register allocateRegister(AssemblyContext ctx) {
    	ctx.addInstruction(Mov.create(index.getLocation(ctx), Register.create("%rax")));
    	ctx.addInstruction(Cmp.create(ImmediateValue.create(variable.getType().getLength()),Register.create("%rax")));
    	ctx.addInstruction(Jae.create(Memory.create("array_index_error")));
        ctx.addInstruction(Xor.create(Register.create("%rdx"), Register.create("%rdx")));
        ctx.addInstruction(Sub.create(index.getLocation(ctx), Register.create("%rdx")));
    	return ((ArrayVariableDescriptor) getVariable()).allocateRegister(ctx, Register.create("%rdx"));
    }
}