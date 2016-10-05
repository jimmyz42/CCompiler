package edu.mit.compilers.ir;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.grammar.DecafParser.BlockContext;
import edu.mit.compilers.grammar.DecafParser.Field_declContext;
import edu.mit.compilers.grammar.DecafParser.StatementContext;

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
            symbolTable.addVariablesFromFieldDecl(checker, fieldDecl);
        }

        for (StatementContext statement : ctx.statement()) {
            statements.add(IrStatement.create(checker, statement));
        }

        checker.popSybmolTable();
    }

    @Override
    public void println(PrintWriter pw, String prefix) {
        pw.println(prefix + "{");
        for (VariableDescriptor var : symbolTable.getVariables().values()) {
            var.println(pw, prefix + "    ");
        }
        for (IrStatement st : statements) {
            pw.println();
            st.println(pw, prefix + "    ");
        }
        pw.println(prefix + "}");
    }
}