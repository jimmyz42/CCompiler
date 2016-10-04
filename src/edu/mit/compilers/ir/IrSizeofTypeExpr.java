package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;

class IrSizeofTypeExpr extends IrExpression {
    private Type type;

    private IrSizeofTypeExpr(Type type) {
        this.type = type;
    }

    public static IrSizeofTypeExpr create(DecafSemanticChecker checker, DecafParser.SizeofTypeExprContext ctx) {
        return new IrSizeofTypeExpr(TypeScalar.create(checker, ctx.type()));
    }
    
    // This is the return type, which is NOT the same as this.type:
    // e.g. `sizeof(bool)` is an int
    @Override
    public Type getExpressionType() {
        return TypeScalar.INT;
    }
}