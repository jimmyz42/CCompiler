package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;

class IrSizeofIdExpr extends IrExpression {
    private IrId id;

    public IrSizeofIdExpr(IrId id) {
        this.id = id;
    }

    public static IrSizeofIdExpr create(DecafSemanticChecker checker, DecafParser.SizeofIdExprContext ctx) {
        return new IrSizeofIdExpr(IrId.create(checker, ctx.ID()));
    }
    
    @Override
    public Type getExpressionType() {
        return TypeScalar.INT;
    }
}