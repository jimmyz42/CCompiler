package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;
import exceptions.UndeclaredIdentifierError;

class IrSizeofIdExpr extends IrExpression {
    private VariableDescriptor variable;

    public IrSizeofIdExpr(VariableDescriptor variable) {
        this.variable = variable;
    }

    public static IrSizeofIdExpr create(DecafSemanticChecker checker, DecafParser.SizeofIdExprContext ctx) {
        String varName = ctx.ID().getText();
        VariableDescriptor variable = checker.currentSymbolTable().getVariable(varName, ctx);
        if (variable == null) {
            throw new UndeclaredIdentifierError("Attempted to apply sizeof to an undeclared variable", ctx);
        }

        return new IrSizeofIdExpr(variable);
    }

    @Override
    public Type getExpressionType() {
        return TypeScalar.INT;
    }
}