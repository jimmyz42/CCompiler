package edu.mit.compilers.ir;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.grammar.DecafParser.Method_argument_declContext;
import edu.mit.compilers.grammar.DecafParser.Method_declContext;

public class MethodDescriptor extends FunctionDescriptor {
    private final List<Type> argumentTypes;
    private final IrBlock body;
    
    public MethodDescriptor(String name, Type returnType, List<Type> argumentTypes, IrBlock body) {
        super(name, returnType);
        this.argumentTypes = argumentTypes;
        this.body = body;
    }

    public static MethodDescriptor create(DecafSemanticChecker checker, Method_declContext ctx, SymbolTable symbolTable) {
        List<Type> argumentTypes = new ArrayList<>();
        IrBlock body = IrBlock.create(checker, ctx.block(), symbolTable);
        String name = ctx.ID().getText();
        Type returnType = ctx.type() == null ? TypeVoid.VOID : TypeScalar.create(checker, ctx.type());
        for (Method_argument_declContext argumentDecl : ctx.method_argument_decl()) {
            Type type = TypeScalar.create(checker, argumentDecl.type());
            String argName = argumentDecl.ID().getText();
            argumentTypes.add(type);
            body.getSymbolTable().addVariable(type, argName);
        }
        
        return new MethodDescriptor(name, returnType, argumentTypes, body);
    }
}
