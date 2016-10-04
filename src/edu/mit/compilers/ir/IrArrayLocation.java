package edu.mit.compilers.ir;

import java.io.PrintWriter;

import edu.mit.compilers.grammar.DecafParser;
import exceptions.TypeMismatchError;
import exceptions.UndeclaredVariableError;

class IrArrayLocation extends IrLocation {
    private VariableDescriptor array;
    private IrExpression index;

    public IrArrayLocation(VariableDescriptor array, IrExpression index) {
        this.array = array;
        this.index = index;
        if (array == null) {
            throw new UndeclaredVariableError("Array is not declared");
        }
        if (!(array.getType() instanceof TypeArray)) {
            throw new TypeMismatchError("Can only index arrays");
        }
        if (index.getExpressionType() != TypeScalar.INT) {
            throw new TypeMismatchError("Array index must be an int");
        }
    }

    public static IrArrayLocation create(DecafSemanticChecker checker, DecafParser.ArrayLocationContext ctx) {
        String varName = ctx.ID().getText();
        VariableDescriptor array = checker.currentSymbolTable().getVariable(varName);
        IrExpression index = IrExpression.create(checker, ctx.expr());
        return new IrArrayLocation(array, index);
    }

    @Override
    public Type getExpressionType() {
        return ((TypeArray) array.getType()).getElementType();
    }
    
    @Override
    public void println(PrintWriter pw, String prefix) {
        super.println(pw, prefix);
        pw.println(prefix + array.getName() + "[");
        index.println(pw, prefix + "  ");
        pw.println(prefix + "] ");
    }
}