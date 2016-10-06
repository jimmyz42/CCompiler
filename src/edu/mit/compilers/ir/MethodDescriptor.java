package edu.mit.compilers.ir;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.mit.compilers.Util;
import edu.mit.compilers.grammar.DecafParser.Method_argument_declContext;
import edu.mit.compilers.grammar.DecafParser.Method_declContext;
import exceptions.TypeMismatchError;

public class MethodDescriptor extends FunctionDescriptor {
    private final List<VariableDescriptor> arguments;
    private final IrBlock body;

    public MethodDescriptor(String name, Type returnType, List<VariableDescriptor> arguments, IrBlock body) {
        super(name, returnType);
        this.arguments = Collections.unmodifiableList(arguments);
        this.body = body;
    }

    public List<VariableDescriptor> getArguments() {
        return arguments;
    }

    public static MethodDescriptor create(DecafSemanticChecker checker, Method_declContext ctx) {
        Type returnType = ctx.type() == null ? TypeVoid.VOID : TypeScalar.create(checker, ctx.type());
        String name = ctx.ID().getText();
        LocalSymbolTable argTable = new LocalSymbolTable(checker.currentSymbolTable());

        IrBlock body = IrBlock.createEmpty(argTable, false);

        List<VariableDescriptor> arguments = new ArrayList<>();
        for (Method_argument_declContext argumentDecl : ctx.method_argument_decl()) {
            Type type = TypeScalar.create(checker, argumentDecl.type());
            String argName = argumentDecl.ID().getText();
            arguments.add(argTable.addVariable(type, argName, argumentDecl));
        }

        if (name.equals("main")) {
            if (returnType != TypeVoid.VOID) {
                throw new TypeMismatchError("Main must return void", ctx);
            }
            if (!arguments.isEmpty()) {
                throw new TypeMismatchError("Main must not take any arguments", ctx);
            }
        }

        return new MethodDescriptor(name, returnType, arguments, body);
    }

    public void loadBody(DecafSemanticChecker checker, Method_declContext ctx) {
        checker.pushMethodDescriptor(this);
        body.loadBlock(checker, ctx.block());
        checker.pushMethodDescriptor(this);
    }

    @Override
    public void prettyPrint(PrintWriter pw, String prefix) {
        pw.print(prefix + getReturnType() + " " + getName() + "(" + Util.joinStrings(arguments) + ") ");
        body.prettyPrint(pw, prefix);
    }
}
