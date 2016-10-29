package edu.mit.compilers.highir.nodes;

import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.Memory;

public class ExternArg extends Ir {
    public static ExternArg create(DecafSemanticChecker checker, DecafParser.Extern_argContext ctx) {
        if (ctx.expr() != null) {
            return Expression.create(checker, ctx.expr());
        } else {
            return new StringLiteral(ctx.STRING_LITERAL().getText());
        }
    }
    
    public Memory getLocation(AssemblyContext ctx) {
    	return ctx.getStackLocation(this);
    }
}