package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;
import exceptions.TypeMismatchException;
import exceptions.UndeclaredVariableException;

class IrIdLocation extends IrLocation {
    private VariableDescriptor variable;

    public IrIdLocation(VariableDescriptor variable) {
        this.variable = variable;
        if (variable == null) {
            throw new UndeclaredVariableException("Variable is not declared");
        }
        if (!(variable.getType() instanceof TypeScalar)) {
            throw new TypeMismatchException("Variable must be a scalar");
        }
    }

    public static IrIdLocation create(DecafSemanticChecker checker, DecafParser.IdLocationContext ctx) {
        String varName = ctx.ID().getText();
        VariableDescriptor variable = checker.currentSymbolTable().getVariable(varName);
        return new IrIdLocation(variable);
    }

    @Override
    public Type getExpressionType() {
        return variable.getType();
    }
}