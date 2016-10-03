package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;

class IrArrayLocation extends IrLocation {
    private IrId id;
    private IrExpression expression;

    public IrArrayLocation(IrId id, IrExpression expression) {
        this.id = id;
        this.expression = expression;
    }

    public static IrArrayLocation create(DecafSemanticChecker checker, DecafParser.ArrayLocationContext ctx) {
         IrId id = IrId.create(checker, ctx.ID());
         IrExpression expression = IrExpression.create(checker, ctx.expr());
         return new IrArrayLocation(id, expression);
    }
}