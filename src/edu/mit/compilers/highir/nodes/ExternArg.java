package edu.mit.compilers.highir.nodes;

import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.Memory;
import edu.mit.compilers.lowir.Register;
import edu.mit.compilers.lowir.Storable;
import edu.mit.compilers.lowir.Storage;

public class ExternArg extends Ir implements Storable {
    public static ExternArg create(DecafSemanticChecker checker, DecafParser.Extern_argContext ctx) {
        if (ctx.expr() != null) {
            return Expression.create(checker, ctx.expr());
        } else {
            return new StringLiteral(ctx.STRING_LITERAL().getText());
        }
    }
    
    @Override
    public Storage getLocation(AssemblyContext ctx) {
    	return ctx.getStackLocation(this);
    }
    
    @Override
    public Register allocateRegister(AssemblyContext ctx) {
    	return ctx.allocateRegister(this);
    }
    
    @Override
    public Register deallocateRegister(AssemblyContext ctx) {
    	return ctx.allocateRegister(this);
    }
}