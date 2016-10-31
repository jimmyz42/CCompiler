package edu.mit.compilers.highir.nodes;

import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.Memory;
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
    
    public Storage getLocation(AssemblyContext ctx) {
    	return ctx.getStackLocation(this);
    }
}