package edu.mit.compilers.highir.symboltable;

import org.antlr.v4.runtime.ParserRuleContext;

import edu.mit.compilers.grammar.DecafParser.FieldContext;
import edu.mit.compilers.grammar.DecafParser.Field_declContext;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.highir.descriptor.FunctionDescriptor;
import edu.mit.compilers.highir.descriptor.VariableDescriptor;
import edu.mit.compilers.highir.nodes.ArrayType;
import edu.mit.compilers.highir.nodes.IntLiteral;
import edu.mit.compilers.highir.nodes.ScalarType;
import edu.mit.compilers.highir.nodes.Type;
import exceptions.BadArraySizeError;

public abstract class SymbolTable {
    protected abstract void checkNamingConflicts(String name, ParserRuleContext ctx);
    // ctx is used for error reporting
    public abstract VariableDescriptor addVariable(Type type, String name, ParserRuleContext ctx);
    public abstract VariableDescriptor getVariable(String name, ParserRuleContext ctx);
    public abstract FunctionDescriptor addFunction(FunctionDescriptor function, ParserRuleContext ctx);
    public abstract FunctionDescriptor getFunction(String name, ParserRuleContext ctx);

    public void addVariablesFromFieldDecl(DecafSemanticChecker checker, Field_declContext ctx) {
        ScalarType type = ScalarType.create(checker, ctx.type());
        for (FieldContext name : ctx.field()) {
            if (name.INT_LITERAL() == null) {
                addVariable(type, name.ID().getText(), name);
            } else {
                long size = IntLiteral.parseLong(name.INT_LITERAL().getText(), ctx);
                if (size <= 0) {
                    throw new BadArraySizeError("Array size must be positive", name);
                }
                addVariable(new ArrayType(size, type), name.ID().getText(), name);
            }
        }
    }
}
