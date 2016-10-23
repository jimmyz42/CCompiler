package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;

import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.highir.descriptor.VariableDescriptor;
import exceptions.TypeMismatchError;
import exceptions.UndeclaredIdentifierError;

public class IdLocation extends Location {

    public IdLocation(VariableDescriptor variable) {
        super(variable);
    }

    public static IdLocation create(DecafSemanticChecker checker, DecafParser.IdLocationContext ctx) {
        String varName = ctx.ID().getText();
        VariableDescriptor variable = checker.currentSymbolTable().getVariable(varName, ctx);
        if (variable == null) {
            throw new UndeclaredIdentifierError("Variable ''" + varName + "' is not declared", ctx);
        }
        if (!(variable.getType() instanceof ScalarType)) {
            throw new TypeMismatchError("Expected a scalar variable, got " + variable.getType(), ctx);
        }
        return new IdLocation(variable);
    }

    @Override
    public void prettyPrint(PrintWriter pw, String prefix) {
        pw.println(prefix + getClass().getSimpleName());
        pw.println(prefix + "-name: " + getVariable().getName());
    }

    @Override
    public void concisePrint(PrintWriter pw, String prefix) {
        pw.print(prefix + getVariable().getName());
    }
}