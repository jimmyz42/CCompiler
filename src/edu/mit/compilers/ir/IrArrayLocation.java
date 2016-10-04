package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;
import exceptions.TypeMismatchException;

class IrArrayLocation extends IrLocation {
    private IrId id;
    private IrExpression expression;

    public IrArrayLocation(IrId id, IrExpression expression) {
        this.id = id;
        this.expression = expression;
        // TODO check that id is indeed an array type
        if (expression.getType() != Type.INT) {
            throw new TypeMismatchException("Array index must be an int");
        }
    }

    public static IrArrayLocation create(DecafSemanticChecker checker, DecafParser.ArrayLocationContext ctx) {
         IrId id = IrId.create(checker, ctx.ID());
         IrExpression expression = IrExpression.create(checker, ctx.expr());
         return new IrArrayLocation(id, expression);
    }

    @Override
    public Type getType() {
        // TODO get the type from the symbol table
        return Type.INT;
    }
}