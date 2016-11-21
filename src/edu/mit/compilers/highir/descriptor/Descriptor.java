package edu.mit.compilers.highir.descriptor;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import edu.mit.compilers.PrettyPrintable;
import edu.mit.compilers.cfg.CFGAble;
import edu.mit.compilers.cfg.CFGContext;
import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.highir.nodes.Expression;
import edu.mit.compilers.highir.nodes.Type;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.Register;
import edu.mit.compilers.lowir.Storable;
import edu.mit.compilers.lowir.Storage;
import edu.mit.compilers.lowir.StorageTuple;
import edu.mit.compilers.optimizer.Optimizable;
import edu.mit.compilers.optimizer.OptimizerContext;

public abstract class Descriptor implements PrettyPrintable, CFGAble, Storable, Optimizable {
	private final String name;
	private final Type returnType;
	private StorageTuple storageTuple;

	public Descriptor(String name, Type returnType) {
		this.name = name;
		this.returnType = returnType;

		this.storageTuple = StorageTuple.create(this);
	}

	public String getName() {
		return name;
	}

	public Type getType() {
		return returnType;
	}

	public StorageTuple getStorageTuple() {
		return storageTuple;
	}

	@Override
	public CFG generateCFG(CFGContext context) {
		return BasicBlock.create(this);
	}

	@Override
	public void cfgPrint(PrintWriter pw, String prefix) {
		pw.println(prefix + getType() + " " + getName());
	}

	@Override
	public Register allocateRegister(AssemblyContext ctx) {
		return ctx.allocateRegister(getStorageTuple());
	}

	@Override
	public Storage getLocation(AssemblyContext ctx) {
		return ctx.getStackLocation(getStorageTuple());
	}

	@Override
	public Storage getLocation(AssemblyContext ctx, boolean forceStackLocation) {
		return getLocation(ctx);
	}

	@Override
	public Set<Descriptor> getConsumedDescriptors() {
		return Collections.emptySet();
	}

	@Override
	public Set<Descriptor> getGeneratedDescriptors() {
		return Collections.emptySet();
	}
    
	@Override
	public Optimizable doConstantFolding() {
		return this;
	}
	
	@Override
	public Optimizable algebraSimplify() {
		return this;
	}

	@Override
	public List<Optimizable> generateTemporaries(OptimizerContext context) {
		return Collections.singletonList((Optimizable)this);
	}

	@Override
	public void doCSE(OptimizerContext ctx) {
	}

	@Override
	public void doConstantPropagation(OptimizerContext ctx){
	}

	@Override
	public void doCopyPropagation(OptimizerContext ctx){
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return hashCode() == obj.hashCode();
	}
}
