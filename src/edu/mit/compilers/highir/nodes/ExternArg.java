package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;

import edu.mit.compilers.cfg.CFGAble;
import edu.mit.compilers.cfg.CFGContext;
import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.Register;
import edu.mit.compilers.lowir.Storable;
import edu.mit.compilers.lowir.Storage;
import edu.mit.compilers.lowir.StorageTuple;
import edu.mit.compilers.optimizer.Optimizable;

abstract public class ExternArg extends Ir implements Storable, CFGAble, Optimizable {

	private final StorageTuple storageTuple;

	public ExternArg() {
		this.storageTuple = StorageTuple.create(this);
	}

    @Override
    public Storage getLocation(AssemblyContext ctx) {
    	return ctx.getStackLocation(getStorageTuple());
    }

    @Override
    public Storage getLocation(AssemblyContext ctx, boolean forceStackLocation ) {
    	return getLocation(ctx);
    }

    public StorageTuple getStorageTuple() {
    	return storageTuple;
    }

    @Override
    public Register allocateRegister(AssemblyContext ctx) {
    	return ctx.allocateRegister(getStorageTuple());
    }

    @Override
    public void cfgPrint(PrintWriter pw, String prefix) {
        pw.println("");
    }

    @Override
    public CFG generateCFG(CFGContext context) {
        return BasicBlock.create(this);
    }
    
    @Override
    public void generateAssembly(AssemblyContext ctx) {
    	
    }
    
    @Override
    public long getNumStackAllocations() {
    	return 0;
    }

    public static ExternArg create(DecafSemanticChecker checker, DecafParser.Extern_argContext ctx) {
        if (ctx.expr() != null) {
            return Expression.create(checker, ctx.expr());
        } else {
            return new StringLiteral(ctx.STRING_LITERAL().getText());
        }
    }
}