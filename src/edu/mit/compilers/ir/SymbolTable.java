package edu.mit.compilers.ir;

import org.antlr.v4.runtime.ParserRuleContext;

import edu.mit.compilers.grammar.DecafParser.FieldContext;
import edu.mit.compilers.grammar.DecafParser.Field_declContext;
import exceptions.BadArraySizeError;

public abstract class SymbolTable {
    protected abstract void checkNamingConflicts(String name, ParserRuleContext ctx);
    // ctx is used for error reporting
    public abstract VariableDescriptor addVariable(Type type, String name, ParserRuleContext ctx);
    public abstract VariableDescriptor getVariable(String name);
    public abstract FunctionDescriptor addFunction(FunctionDescriptor function, ParserRuleContext ctx);
    public abstract FunctionDescriptor getFunction(String name);

    public void addVariablesFromFieldDecl(DecafSemanticChecker checker, Field_declContext ctx) {
        Type type = TypeScalar.create(checker, ctx.type());
        for (FieldContext name : ctx.field()) {
            if (name.INT_LITERAL() == null) {
                addVariable(type, name.ID().getText(), name);
            } else {
                long size = IrIntLiteral.parseLong(name.INT_LITERAL().getText(), ctx);
                if (size <= 0) {
                    throw new BadArraySizeError("Array size must be positive", name);
                }
                addVariable(new TypeArray(size, type), name.ID().getText(), name);
            }
        }
    }
}
