package edu.mit.compilers.ir;

import java.io.PrintWriter;

import edu.mit.compilers.grammar.DecafParser.Extern_declContext;
import edu.mit.compilers.grammar.DecafParser.Field_declContext;
import edu.mit.compilers.grammar.DecafParser.Method_declContext;
import edu.mit.compilers.grammar.DecafParser.ProgramContext;

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
            symbolTable.addFunction(MethodDescriptor.create(checker, methodDecl));
        }
        
        checker.popSybmolTable();
        return new IrProgram(symbolTable);
    }


    @Override
    public void println(PrintWriter pw, String prefix) {
        for (VariableDescriptor var : symbolTable.getVariables().values()) {
            var.println(pw, prefix);
        }
        for (FunctionDescriptor fn : symbolTable.getFunctions().values()) {
            fn.println(pw, prefix);
        }
    }
}