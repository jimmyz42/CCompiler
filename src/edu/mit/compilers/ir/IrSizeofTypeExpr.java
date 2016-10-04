package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;

class IrSizeofTypeExpr extends IrExpression {
    private IrType type;

    private IrSizeofTypeExpr(IrType type) {
        this.type = type;
    }

    public static IrSizeofTypeExpr create(DecafSemanticChecker checker, DecafParser.SizeofTypeExprContext ctx) {
        return new IrSizeofTypeExpr(IrType.create(checker, ctx.type()));
    }
}