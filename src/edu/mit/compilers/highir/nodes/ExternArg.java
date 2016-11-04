package edu.mit.compilers.highir.nodes;

import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.Memory;
import edu.mit.compilers.lowir.Register;
import edu.mit.compilers.lowir.Storable;
import edu.mit.compilers.lowir.Storage;
import edu.mit.compilers.lowir.StorageTuple;

abstract public class ExternArg extends Ir implements Storable {

	private final StorageTuple storageTuple;

	public ExternArg() {
		this.storageTuple = StorageTuple.create(this);
	}

    @Override
    public Storage getLocation(AssemblyContext ctx) {
    	//most expressions and literals don't need an index parameter
    	return ctx.getStackLocation(getStorageTuple());
    }

    public StorageTuple getStorageTuple() {
    	return storageTuple;
    }

    @Override
    public Register allocateRegister(AssemblyContext ctx) {
    	return ctx.allocateRegister(getStorageTuple());
    }

    public static ExternArg create(DecafSemanticChecker checker, DecafParser.Extern_argContext ctx) {
        if (ctx.expr() != null) {
            return Expression.create(checker, ctx.expr());
        } else {
            return new StringLiteral(ctx.STRING_LITERAL().getText());
        }
    }
}