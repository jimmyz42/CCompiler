package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;

import java.util.ArrayList;

import edu.mit.compilers.grammar.DecafParser.Extern_declContext;
import edu.mit.compilers.grammar.DecafParser.Field_declContext;
import edu.mit.compilers.grammar.DecafParser.Method_declContext;
import edu.mit.compilers.grammar.DecafParser.ProgramContext;

import edu.mit.compilers.PrettyPrintable;

import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.highir.descriptor.*;
import edu.mit.compilers.highir.symboltable.GlobalSymbolTable;

import edu.mit.compilers.cfg.CFGAble;
import edu.mit.compilers.cfg.components.BasicBlock;

import exceptions.SemanticError;
import exceptions.UndeclaredIdentifierError;

public class Program extends Ir implements PrettyPrintable, CFGAble {
    private final GlobalSymbolTable symbolTable;

    public Program(GlobalSymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }


    public static Program create(DecafSemanticChecker checker, ProgramContext ctx) {
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

        FunctionDescriptor fnMain = null;
        try {
            fnMain = symbolTable.getFunction("main", ctx);
        } catch (SemanticError e) {
            checker.handleSemanticError(e);
        }
        if (fnMain == null) {
            checker.handleSemanticError(new UndeclaredIdentifierError("No main method found", ctx));
        } else if (!(fnMain.getType() instanceof VoidType)) {
            checker.handleSemanticError(new UndeclaredIdentifierError("Main method must have type void", ctx));
        }

        return new Program(symbolTable);
    }

    @Override
    public void prettyPrint(PrintWriter pw, String prefix) {
        for (Descriptor desc : symbolTable.getDescriptors().values()) {
            desc.prettyPrint(pw, prefix);
        }
    }

    @Override
    public void concisePrint(PrintWriter pw, String prefix) {
        symbolTable.concisePrint(pw, prefix);
        //TODO print the other types of blocks
    }

    public BasicBlock generateCFG() {
        ArrayList<CFGAble> components = new ArrayList<>();
        //TODO: get fields and put them into the components
        return symbolTable.generateCFG();
        //TODO merge or append blocks for generated methods to the program block
    }
}