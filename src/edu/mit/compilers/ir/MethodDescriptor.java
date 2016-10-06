package edu.mit.compilers.ir;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.mit.compilers.Util;
import edu.mit.compilers.grammar.DecafParser.Method_argument_declContext;
import edu.mit.compilers.grammar.DecafParser.Method_declContext;

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

        IrBlock body = IrBlock.createEmpty(checker, argTable);

        List<VariableDescriptor> arguments = new ArrayList<>();
        for (Method_argument_declContext argumentDecl : ctx.method_argument_decl()) {
            Type type = TypeScalar.create(checker, argumentDecl.type());
            String argName = argumentDecl.ID().getText();
            arguments.add(argTable.addVariable(type, argName));
        }

        return new MethodDescriptor(name, returnType, arguments, body);
    }

    public void loadBody(DecafSemanticChecker checker, Method_declContext ctx) {
        body.loadBlock(checker, ctx.block());
    }

    @Override
    public void prettyPrint(PrintWriter pw, String prefix) {
        pw.println(prefix + getClass().getSimpleName());
        pw.print(prefix + "-returnType: ");
        getReturnType().prettyPrint(pw,"");
        pw.println("\n" + prefix + "-name: " + getName());
        pw.print(prefix + "-parameters:\n");
        for(VariableDescriptor var : arguments ){
        	var.prettyPrint(pw, prefix + "    ");
        }
        pw.println();
        body.prettyPrint(pw, prefix + "    ");
        pw.println(prefix + "end " + getName() + " " + getClass().getSimpleName());
        pw.println();
    }
}
