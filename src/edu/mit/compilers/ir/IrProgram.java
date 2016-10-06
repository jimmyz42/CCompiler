package edu.mit.compilers.ir;

import java.io.PrintWriter;

import edu.mit.compilers.grammar.DecafParser.Extern_declContext;
import edu.mit.compilers.grammar.DecafParser.Field_declContext;
import edu.mit.compilers.grammar.DecafParser.Method_declContext;
import edu.mit.compilers.grammar.DecafParser.ProgramContext;
import exceptions.SemanticError;
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
            try {
                symbolTable.addFunction(new ExternDescriptor(externDecl.ID().getText()), externDecl);
            } catch (SemanticError e) {
                checker.handleSemanticError(e);
            }
        }

        for (Field_declContext fieldDecl : ctx.field_decl()) {
            try {
                symbolTable.addVariablesFromFieldDecl(checker, fieldDecl);
            } catch (SemanticError e) {
                checker.handleSemanticError(e);
            }
        }

        for (Method_declContext methodDecl : ctx.method_decl()) {
            try {
                MethodDescriptor method = MethodDescriptor.create(checker, methodDecl);
                symbolTable.addFunction(method, ctx);
                method.loadBody(checker, methodDecl);
            } catch (SemanticError e) {
                checker.handleSemanticError(e);
            }
        }

        checker.popSybmolTable();

        FunctionDescriptor fnMain = symbolTable.getFunction("main");
        if (fnMain == null) {
            throw new UndeclaredIdentifierError("No main method found", ctx);
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