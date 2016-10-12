package edu.mit.compilers.highir.nodes;

import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;

public class SizeofTypeExpr extends Expression {
    private Type type;

    private SizeofTypeExpr(Type type) {
        this.type = type;
    }

    public static SizeofTypeExpr create(DecafSemanticChecker checker, DecafParser.SizeofTypeExprContext ctx) {
        return new SizeofTypeExpr(ScalarType.create(checker, ctx.type()));
    }
    
    // This is the return type, which is NOT the same as this.type:
    // e.g. `sizeof(bool)` is an int
    @Override
    public Type getExpressionType() {
        return ScalarType.INT;
    }
}