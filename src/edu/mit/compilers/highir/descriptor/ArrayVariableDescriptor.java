package edu.mit.compilers.highir.descriptor;

import java.util.ArrayList;

import edu.mit.compilers.highir.nodes.ArrayType;
import edu.mit.compilers.highir.nodes.ScalarType;
import edu.mit.compilers.highir.nodes.Type;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.ImmediateValue;
import edu.mit.compilers.lowir.Memory;
import edu.mit.compilers.lowir.Register;
import edu.mit.compilers.lowir.Storage;
import edu.mit.compilers.lowir.StorageTuple;
import edu.mit.compilers.lowir.instructions.Directive;
import edu.mit.compilers.lowir.instructions.Lea;
import edu.mit.compilers.lowir.instructions.Mov;

public class ArrayVariableDescriptor extends VariableDescriptor {
	private ArrayList<ScalarVariableDescriptor> storedDescriptors;

	public ArrayVariableDescriptor(String name, ArrayType type, boolean isGlobal, ArrayList<ScalarVariableDescriptor> storedDescriptors) {
		super(name, type, isGlobal);
		this.storedDescriptors = storedDescriptors;
	}

	@Override
	public void generateAssembly(AssemblyContext ctx) {
		if(isGlobal())
			ctx.addInstruction(Directive.create(".comm " + getName() + ", "
					+ getType().getSize() + ", "
					+ getType().getSize()));
		else {

			for(int i =0; i < storedDescriptors.size(); i ++) {
				storedDescriptors.get(i).generateAssembly(ctx);
			}
		}
	}

	@Override
	public void storeStack(int index, Register reg, AssemblyContext ctx) {
		storedDescriptors.get(index).storeStack(index, reg, ctx);
	}

	@Override
	public void setStackPosition(int index, int position, AssemblyContext ctx) {
		storedDescriptors.get(index).setStackPosition(index, position, ctx);
	}

	@Override
	public ArrayType getType() {
		return (ArrayType) super.getType();
	}

	@Override
	public long getNumStackAllocations() {
		if(isGlobal()) return 0;
		return getType().getLength();
	}

	public Storage getLocation(AssemblyContext ctx, Register index) {
		if(isGlobal()) {
			ctx.addInstruction(Lea.create(Memory.create(getName() + "(%rip)"), Register.create("%rax")));
			return Memory.create("(%rax, " + index.getName() + ", 8)");
		
		}
		StorageTuple tupleWithIndex = StorageTuple.create(storedDescriptors.get(0).getStorageTuple().base, index);
		return ctx.getStackLocation(tupleWithIndex, true);
	}

	public Register allocateRegister(AssemblyContext ctx, Register index) {
		if(isGlobal()) {
			Register reg = ctx.allocateRegister();
			ctx.addInstruction(Mov.create(getLocation(ctx, index), reg));
			return reg;
		}
		StorageTuple tupleWithIndex = StorageTuple.create(storedDescriptors.get(0).getStorageTuple().base, index);
		return ctx.allocateRegister(tupleWithIndex, true);
	}

	public static ArrayVariableDescriptor create(String name, ArrayType type, boolean isGlobal) {
		ArrayList<ScalarVariableDescriptor> storedDescriptors = new ArrayList<ScalarVariableDescriptor>();
		for(int i = 0; i < type.getLength(); i++) {
			storedDescriptors.add(ScalarVariableDescriptor.create(name, type.getElementType(), isGlobal));
		}
		return new ArrayVariableDescriptor(name, type, isGlobal, storedDescriptors);
	}
}
