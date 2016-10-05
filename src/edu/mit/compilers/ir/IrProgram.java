package edu.mit.compilers.ir;

import java.io.PrintWriter;

import edu.mit.compilers.grammar.DecafParser.Extern_declContext;
import edu.mit.compilers.grammar.DecafParser.Field_declContext;
import edu.mit.compilers.grammar.DecafParser.Method_declContext;
import edu.mit.compilers.grammar.DecafParser.ProgramContext;
import exceptions.TypeMismatchError;
import exceptions.UndeclaredIdentifierError;

class IrProgram extends Ir {
    private final GlobalSymbolTable symbolTable;

    public IrProgram(GlobalSymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }


    public static IrProgram create(DecafSemanticChecker checker, ProgramContext ctx) {
        GlobalSymbolTable symbolTable = new GlobalSymbolTable();
        checker.pushSymbolTable(symbolTable);

        for (Extern_declContext externDecl : ctx.extern_decl()) {
            symbolTable.addFunction(new ExternDescriptor(externDecl.ID().getText()));
        }

        for (Field_declContext fieldDecl : ctx.field_decl()) {
            symbolTable.addVariablesFromFieldDecl(checker, fieldDecl);
        }

        for (Method_declContext methodDecl : ctx.method_decl()) {
            MethodDescriptor method = MethodDescriptor.create(checker, methodDecl);
            symbolTable.addFunction(method);
            method.loadBody(checker, methodDecl);
        }

        checker.popSybmolTable();

        FunctionDescriptor fnMain = symbolTable.getFunction("main");
        if (fnMain == null) {
            throw new UndeclaredIdentifierError("No main method found");
        }
        if (!(fnMain instanceof MethodDescriptor)) {
            throw new TypeMismatchError("Main must be a method (not an extern)");
        }
        MethodDescriptor main = (MethodDescriptor) fnMain;
        if (main.getReturnType() != TypeVoid.VOID) {
            throw new TypeMismatchError("Main must return void");
        }
        if (!main.getArguments().isEmpty()) {
            throw new TypeMismatchError("Main must not take any arguments");
        }

        return new IrProgram(symbolTable);
    }


    @Override
    public void prettyPrint(PrintWriter pw, String prefix) {
        for (VariableDescriptor var : symbolTable.getVariables().values()) {
            var.prettyPrint(pw, prefix);
        }
        for (FunctionDescriptor fn : symbolTable.getFunctions().values()) {
            fn.prettyPrint(pw, prefix);
        }
    }
}