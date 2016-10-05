package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;
import exceptions.UndeclaredIdentifierError;

class IrSizeofIdExpr extends IrExpression {
    private VariableDescriptor var;

    public IrSizeofIdExpr(VariableDescriptor var) {
        this.var = var;
        if (var == null) {
            throw new UndeclaredIdentifierError("Attempted to apply sizeof to an undeclared variable");
        }
    }

    public static IrSizeofIdExpr create(DecafSemanticChecker checker, DecafParser.SizeofIdExprContext ctx) {
        String varName = ctx.ID().getText();
        return new IrSizeofIdExpr(checker.currentSymbolTable().getVariable(varName));
    }
    
    @Override
    public Type getExpressionType() {
        return TypeScalar.INT;
    }
}