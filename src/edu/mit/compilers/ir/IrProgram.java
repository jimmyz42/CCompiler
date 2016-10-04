package edu.mit.compilers.ir;

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
            symbolTable.addFunction(MethodDescriptor.create(checker, methodDecl, symbolTable));
        }
        
        checker.popSybmolTable();
        return new IrProgram(symbolTable);
    }
}