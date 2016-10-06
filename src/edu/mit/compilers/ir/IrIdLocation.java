package edu.mit.compilers.ir;

import java.io.PrintWriter;

import edu.mit.compilers.grammar.DecafParser;
import exceptions.TypeMismatchError;
import exceptions.UndeclaredIdentifierError;

class IrIdLocation extends IrLocation {
    private VariableDescriptor variable;

    public IrIdLocation(VariableDescriptor variable) {
        this.variable = variable;
    }

    public static IrIdLocation create(DecafSemanticChecker checker, DecafParser.IdLocationContext ctx) {
        String varName = ctx.ID().getText();
        VariableDescriptor variable = checker.currentSymbolTable().getVariable(varName);
        if (variable == null) {
            throw new UndeclaredIdentifierError("Variable ''" + varName + "' is not declared");
        }
        if (!(variable.getType() instanceof TypeScalar)) {
            throw new TypeMismatchError("Variable must be a scalar");
        }
        return new IrIdLocation(variable);
    }

    @Override
    public Type getExpressionType() {
        return variable.getType();
    }

    @Override
    public void prettyPrint(PrintWriter pw, String prefix) {
    	pw.println(prefix + getClass().getSimpleName());
        pw.println(prefix + "-name: " + variable.getName());
    }
}