package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;

class IrIdLocation extends IrLocation {
    private IrId id;

    public IrIdLocation(IrId id) {
        this.id = id;
    }

    public static IrIdLocation create(DecafSemanticChecker checker, DecafParser.IdLocationContext ctx) {
        IrId id = IrId.create(checker, ctx);
        return new IrIdLocation(id);
    }

    @Override
    public Type getExpressionType() {
        // TODO get the type from the symbol table
        return TypeScalar.INT;
    }
}