package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser.FieldContext;
import edu.mit.compilers.grammar.DecafParser.Field_declContext;

public abstract class SymbolTable {
    public abstract VariableDescriptor addVariable(Type type, String name);
    public abstract VariableDescriptor getVariable(String name);
    public abstract FunctionDescriptor addFunction(FunctionDescriptor function);
    public abstract FunctionDescriptor getFunction(String name);
    // TODO addMethod, getMethod
    
    public void addVariablesFromFieldDecl(DecafSemanticChecker checker, Field_declContext ctx) {
        Type type = TypeScalar.create(checker, ctx.type());
        for (FieldContext name : ctx.field()) {
            if (name.INT_LITERAL() == null) {
                addVariable(type, name.ID().getText());
            } else {
                int size = IrIntLiteral.parseInt(name.INT_LITERAL().getText());
                addVariable(new TypeArray(size, type), name.ID().getText());
            }
        }
    }
}
