package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;

import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.highir.descriptor.VariableDescriptor;
import exceptions.UndeclaredIdentifierError;

public class SizeofIdExpr extends Expression {
    private VariableDescriptor variable;

    public SizeofIdExpr(VariableDescriptor variable) {
        this.variable = variable;
    }

    public static SizeofIdExpr create(DecafSemanticChecker checker, DecafParser.SizeofIdExprContext ctx) {
        String varName = ctx.ID().getText();
        VariableDescriptor variable = checker.currentSymbolTable().getVariable(varName, ctx);
        if (variable == null) {
            throw new UndeclaredIdentifierError("Attempted to apply sizeof to an undeclared variable", ctx);
        }

        return new SizeofIdExpr(variable);
    }

    @Override
    public Type getExpressionType() {
        return ScalarType.INT;
    }

    @Override
    public void cfgPrint(PrintWriter pw, String prefix) {
    	pw.print(prefix + "sizeof(" + variable.getName() + ")");
    }
}