package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;
import exceptions.TypeMismatchError;
import exceptions.UndeclaredVariableError;

class IrIdLocation extends IrLocation {
    private VariableDescriptor variable;

    public IrIdLocation(VariableDescriptor variable) {
        this.variable = variable;
        if (variable == null) {
            throw new UndeclaredVariableError("Variable is not declared");
        }
        if (!(variable.getType() instanceof TypeScalar)) {
            throw new TypeMismatchError("Variable must be a scalar");
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