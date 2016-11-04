package edu.mit.compilers.highir.descriptor;

import edu.mit.compilers.highir.nodes.ArrayType;
import edu.mit.compilers.highir.nodes.ScalarType;
import edu.mit.compilers.highir.nodes.Type;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.ImmediateValue;
import edu.mit.compilers.lowir.Memory;
import edu.mit.compilers.lowir.Register;
import edu.mit.compilers.lowir.Storage;
import edu.mit.compilers.lowir.instructions.Directive;
import edu.mit.compilers.lowir.instructions.Mov;

public class ScalarVariableDescriptor extends VariableDescriptor {
	public ScalarVariableDescriptor(String name, ScalarType type, boolean isGlobal) {
		super(name, type, isGlobal);
	}

	@Override
	public void generateAssembly(AssemblyContext ctx) {
		if(isGlobal())
			ctx.addInstruction(Directive.create(".comm " + getName() + ", " + getType().getSize() + ", " + getType().getSize()));
		else
			ctx.storeStack(getStorageTuple(), ImmediateValue.create(0));
	}

	@Override
	public void storeStack(int index, Register reg, AssemblyContext ctx) {
		if(isGlobal()) return;
		ctx.storeStack(getStorageTuple(), reg);
	}

	@Override
	public void setStackPosition(int index, int position, AssemblyContext ctx) {
		if(isGlobal()) return;
		ctx.setStackPosition(getStorageTuple(), position);
	}

	@Override
	public long getNumStackAllocations() {
		if(isGlobal()) return 0;
		return 1;
	}

    @Override
    public Register allocateRegister(AssemblyContext ctx) {
		if(isGlobal()) {
			Register reg = ctx.allocateRegister();
			ctx.addInstruction(Mov.create(getLocation(ctx), reg));
			return reg;
		}
    	return ctx.allocateRegister(getStorageTuple());
    }

    @Override
    public Storage getLocation(AssemblyContext ctx) {
		if(isGlobal()) return Memory.create(getName() + "(%rip)");
    	return ctx.getStackLocation(getStorageTuple());
    }

	public static ScalarVariableDescriptor create(String name, ScalarType type, boolean isGlobal) {
		return new ScalarVariableDescriptor(name, type, isGlobal);
	}
}
