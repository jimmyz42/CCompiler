package edu.mit.compilers.ir;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.grammar.DecafParser.BlockContext;
import edu.mit.compilers.grammar.DecafParser.Field_declContext;
import edu.mit.compilers.grammar.DecafParser.StatementContext;
import exceptions.SemanticError;

class IrBlock extends Ir {
    private final LocalSymbolTable symbolTable;
    private List<IrStatement> statements;

    public IrBlock(LocalSymbolTable symbolTable, List<IrStatement> statements) {
        this.symbolTable = symbolTable;
        this.statements = statements;
    }

    public LocalSymbolTable getSymbolTable() {
        return symbolTable;
    }

    public static IrBlock create(DecafSemanticChecker checker, BlockContext ctx, SymbolTable parentTable) {
        IrBlock block = createEmpty(checker, parentTable);
        block.loadBlock(checker, ctx);
        return block;
    }

    public static IrBlock create(DecafSemanticChecker checker, BlockContext ctx) {
        IrBlock block = createEmpty(checker);
        block.loadBlock(checker, ctx);
        return block;
    }

    public static IrBlock createEmpty(DecafSemanticChecker checker, SymbolTable parentTable) {
        List<IrStatement> statements = new ArrayList<>();
        LocalSymbolTable symbolTable = new LocalSymbolTable(parentTable);
        return new IrBlock(symbolTable, statements);
    }

    public static IrBlock createEmpty(DecafSemanticChecker checker) {
        SymbolTable parentTable = checker.currentSymbolTable();
        List<IrStatement> statements = new ArrayList<>();
        LocalSymbolTable symbolTable = new LocalSymbolTable(parentTable);
        return new IrBlock(symbolTable, statements);
    }

    public void loadBlock(DecafSemanticChecker checker, BlockContext ctx) {
        checker.pushSymbolTable(symbolTable);

        for (Field_declContext fieldDecl : ctx.field_decl()) {
            try {
                symbolTable.addVariablesFromFieldDecl(checker, fieldDecl);
            } catch (SemanticError e) {
                checker.handleSemanticError(e);
            }
        }

        for (StatementContext statement : ctx.statement()) {
            try {
                statements.add(IrStatement.create(checker, statement));
            } catch (SemanticError e) {
                checker.handleSemanticError(e);
            }
        }

        checker.popSybmolTable();
    }

    @Override
    public void prettyPrint(PrintWriter pw, String prefix) {
        pw.println(prefix + getClass().getSimpleName());
        for (VariableDescriptor var : symbolTable.getVariables().values()) {
            var.prettyPrint(pw, prefix + "    ");
            pw.println();
        }
        for (IrStatement st : statements) {
            st.prettyPrint(pw, prefix + "    ");
            pw.println();
        }
        pw.println(prefix + "end " + getClass().getSimpleName());
    }
}