package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;

class IrExternArg extends Ir {
    public static IrExternArg create(DecafSemanticChecker checker, DecafParser.Extern_argContext ctx) {
        if (ctx.expr() != null) {
            return IrExpression.create(checker, ctx.expr());
        } else {
            return new IrStringLiteral(ctx.STRING_LITERAL().getText());
        }
    }
}