package edu.mit.compilers.highir.descriptor;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.Set;

import edu.mit.compilers.PrettyPrintable;
import edu.mit.compilers.cfg.CFGAble;
import edu.mit.compilers.cfg.CFGContext;
import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.highir.nodes.Type;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.Register;
import edu.mit.compilers.lowir.Storable;
import edu.mit.compilers.lowir.Storage;
import edu.mit.compilers.lowir.StorageTuple;

public abstract class Descriptor implements PrettyPrintable, CFGAble, Storable {
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
}