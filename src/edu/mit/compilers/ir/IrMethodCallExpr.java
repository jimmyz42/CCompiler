package edu.mit.compilers.ir;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.mit.compilers.grammar.DecafParser.Extern_argContext;
import edu.mit.compilers.grammar.DecafParser.Method_callContext;
import exceptions.MethodCallException;

class IrMethodCallExpr extends IrExpression {
    private final FunctionDescriptor function;
    private final List<IrExternArg> arguments;
    
    public IrMethodCallExpr(FunctionDescriptor function, List<IrExternArg> arguments) {
        this.function = function;
        this.arguments = Collections.unmodifiableList(arguments);
        
        if (function instanceof MethodDescriptor) {
            MethodDescriptor method = (MethodDescriptor) function;
            List<VariableDescriptor> expectedArgs = method.getArguments();
            if (arguments.size() != expectedArgs.size()) {
                throw new MethodCallException("Expected " + expectedArgs.size() + " arguments, got " + arguments.size());
            }
            for (int i = 0; i < arguments.size(); i++) {
                if (!(arguments.get(i) instanceof IrExpression)) {
                    throw new MethodCallException("Can't have string literals as method arguments");
                }
                IrExpression arg = (IrExpression) arguments.get(i);
                if (arg.getExpressionType() != expectedArgs.get(i).getType()) {
                    // 1-based indexing for arguments in error messages
                    throw new MethodCallException("Argument #" + (i + 1) + " has type " + arg.getExpressionType() + ", expected " + expectedArgs.get(i).getType());
                }
            }
        }
    }

    @Override
    public Type getExpressionType() {
        return function.getReturnType();
    }

    public static IrMethodCallExpr create(DecafSemanticChecker checker, Method_callContext ctx) {
        String functionName = ctx.ID().getText();
        FunctionDescriptor function = checker.currentSymbolTable().getFunction(functionName);
        
        List<IrExternArg> arguments = new ArrayList<>();
        for (Extern_argContext argument : ctx.extern_arg()) {
            arguments.add(IrExternArg.create(checker, argument));
        }
        
        return new IrMethodCallExpr(function, arguments);
    }
}