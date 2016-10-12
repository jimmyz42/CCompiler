package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;

import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.highir.descriptor.VariableDescriptor;
import exceptions.TypeMismatchError;
import exceptions.UndeclaredIdentifierError;

public class ArrayLocation extends Location {
    private VariableDescriptor array;
    private Expression index;

    public ArrayLocation(VariableDescriptor array, Expression index) {
        this.array = array;
        this.index = index;
    }

    public static ArrayLocation create(DecafSemanticChecker checker, DecafParser.ArrayLocationContext ctx) {
        String varName = ctx.ID().getText();
        VariableDescriptor array = checker.currentSymbolTable().getVariable(varName, ctx);
        Expression index = Expression.create(checker, ctx.expr());

        if (array == null) {
            throw new UndeclaredIdentifierError("Array is not declared", ctx);
        }
        if (!(array.getType() instanceof ArrayType)) {
            throw new TypeMismatchError("Can only index arrays", ctx);
        }
        if (index.getExpressionType() != ScalarType.INT) {
            throw new TypeMismatchError("Array index must be an int", ctx.expr());
        }

        return new ArrayLocation(array, index);
    }

    @Override
    public Type getExpressionType() {
        return ((ArrayType) array.getType()).getElementType();
    }

    @Override
    public void prettyPrint(PrintWriter pw, String prefix) {
        super.prettyPrint(pw, prefix);
        pw.println(prefix + array.getName() + "[");
        index.prettyPrint(pw, prefix + "  ");
        pw.println(prefix + "] ");
    }
}