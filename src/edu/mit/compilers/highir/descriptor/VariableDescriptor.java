package edu.mit.compilers.highir.descriptor;

import java.io.PrintWriter;

import edu.mit.compilers.PrettyPrintable;
import edu.mit.compilers.cfg.CFGContext;
import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.highir.nodes.ArrayType;
import edu.mit.compilers.highir.nodes.ScalarType;
import edu.mit.compilers.highir.nodes.Type;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.ImmediateValue;
import edu.mit.compilers.lowir.Register;
import edu.mit.compilers.lowir.Storage;

public abstract class VariableDescriptor extends Descriptor {
	public boolean isGlobal;
	
	public VariableDescriptor(String name, Type type, boolean isGlobal) {
		super(name, type);
		this.isGlobal = isGlobal;
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

	public static VariableDescriptor create(String name, Type type, boolean isGlobal) {
		if(type instanceof ScalarType) {
			return ScalarVariableDescriptor.create(name, (ScalarType) type, isGlobal);
		}
		return ArrayVariableDescriptor.create(name, (ArrayType) type, isGlobal);
	}
}
