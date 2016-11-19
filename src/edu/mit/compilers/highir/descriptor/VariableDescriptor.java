package edu.mit.compilers.highir.descriptor;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.Set;

import edu.mit.compilers.highir.nodes.ArrayType;
import edu.mit.compilers.highir.nodes.ScalarType;
import edu.mit.compilers.highir.nodes.Type;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.Register;

public abstract class VariableDescriptor extends Descriptor {
	public boolean isGlobal;
	private boolean isTemp;
	
	public VariableDescriptor(String name, Type type, boolean isGlobal) {
		super(name, type);
		this.isGlobal = isGlobal;
		this.isTemp = false;
	}

	public void setToTemp(){
		this.isTemp = true;
	}
	
	public boolean isTemp(){
		return this.isTemp;
	}

	@Override
	public void prettyPrint(PrintWriter pw, String prefix) {
		pw.println(prefix + "VariableDescriptor: ");
		pw.print(prefix + "-type: ");
		getType().prettyPrint(pw, "");
		pw.println("\n" + prefix + "-name: " + getName());

	}

	public boolean isGlobal() {
		return isGlobal;
	}
	
	@Override
	public String toString() {
		return getType() + " " + getName();
	}

	abstract public void storeStack(int index, Register reg, AssemblyContext ctx);
	abstract public void setStackPosition(int index, int position, AssemblyContext ctx);

	@Override
	public Set<Descriptor> getConsumedDescriptors() {
		return Collections.emptySet();
	}

	@Override
	public Set<Descriptor> getGeneratedDescriptors() {
		return Collections.singleton((Descriptor)this);
	}

	public static VariableDescriptor create(String name, Type type, boolean isGlobal) {
		if(type instanceof ScalarType) {
			return ScalarVariableDescriptor.create(name, (ScalarType) type, isGlobal);
		}
		return ArrayVariableDescriptor.create(name, (ArrayType) type, isGlobal);
	}
}
