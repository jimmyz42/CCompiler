package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import edu.mit.compilers.grammar.DecafParser.Extern_argContext;
import edu.mit.compilers.grammar.DecafParser.Method_callContext;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.highir.descriptor.*;
import exceptions.MethodCallException;
import exceptions.UndeclaredIdentifierError;

public class MethodCallExpr extends Expression {
    private final FunctionDescriptor function;
    private final List<ExternArg> arguments;

    public MethodCallExpr(FunctionDescriptor function, List<ExternArg> arguments) {
        this.function = function;
        this.arguments = Collections.unmodifiableList(arguments);
    }

    @Override
    public Type getExpressionType() {
        return function.getType();
    }

    public static MethodCallExpr create(DecafSemanticChecker checker, Method_callContext ctx) {
        String functionName = ctx.ID().getText();
        FunctionDescriptor function = checker.currentSymbolTable().getFunction(functionName, ctx);

        List<ExternArg> arguments = new ArrayList<>();
        for (Extern_argContext argument : ctx.extern_arg()) {
            arguments.add(ExternArg.create(checker, argument));
        }

        if (function == null) {
            throw new UndeclaredIdentifierError("Tried to call an undeclared function", ctx);
        }

        if (function instanceof MethodDescriptor) {
            MethodDescriptor method = (MethodDescriptor) function;
            List<VariableDescriptor> expectedArgs = method.getArguments();
            if (arguments.size() != expectedArgs.size()) {
                throw new MethodCallException("Expected " + expectedArgs.size() + " arguments, got " + arguments.size(), ctx);
            }
            for (int i = 0; i < arguments.size() && i <= Integer.MAX_VALUE; i++) {
                if (!(arguments.get(i) instanceof Expression)) {
                    throw new MethodCallException("Can't have string literals as method arguments", ctx.extern_arg(i));
                }
                Expression arg = (Expression) arguments.get(i);
                if (arg.getExpressionType() != expectedArgs.get(i).getType()) {
                    // 1-based indexing for arguments in error messages
                    throw new MethodCallException("Argument #" + (i + 1) + " has type " + arg.getExpressionType() +
                    ", expected " + expectedArgs.get(i).getType(), ctx.extern_arg(i));
                }
            }
        }

        return new MethodCallExpr(function, arguments);
    }

    @Override
    public void prettyPrint(PrintWriter pw, String prefix) {
        super.prettyPrint(pw, prefix);
        pw.print(prefix + "-function descriptor: ");
        function.getType().prettyPrint(pw, "");
        pw.print(" ");
        pw.println(function.getName());
        pw.println(prefix + "-passed in arguments:");
        for (ExternArg arg : arguments){
            arg.prettyPrint(pw, prefix+"    ");
        }
    }

    @Override
    public void cfgPrint(PrintWriter pw, String prefix) {
        pw.print(prefix + function.getName() + "(");
        for(Iterator<ExternArg> it = arguments.iterator(); it.hasNext();) {
            it.next().cfgPrint(pw, "");
            if (it.hasNext()) {
                pw.print(", ");
            }
        }
        pw.print(")");
    }
}