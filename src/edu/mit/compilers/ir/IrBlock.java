package edu.mit.compilers.ir;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.grammar.DecafParser.BlockContext;
import edu.mit.compilers.grammar.DecafParser.Field_declContext;
import edu.mit.compilers.grammar.DecafParser.StatementContext;
import edu.mit.compilers.grammar.DecafParser.BreakStmtContext;
import edu.mit.compilers.grammar.DecafParser.ContinueStmtContext;
import exceptions.SemanticError;
import exceptions.ImproperEscapeError;

class IrBlock extends Ir {
    private final LocalSymbolTable symbolTable;
    private List<IrStatement> statements;
    private boolean allowEscapeStmts;

    public IrBlock(LocalSymbolTable symbolTable, List<IrStatement> statements, boolean allowEscapeStmts) {
        this.symbolTable = symbolTable;
        this.statements = statements;
        this.allowEscapeStmts = allowEscapeStmts;
    }

    public LocalSymbolTable getSymbolTable() {
        return symbolTable;
    }

    public static IrBlock create(DecafSemanticChecker checker, BlockContext ctx, boolean allowEscapeStmts) {
        SymbolTable parentTable = checker.currentSymbolTable();
        return create(checker, ctx, parentTable, allowEscapeStmts);
    }

    public static IrBlock create(DecafSemanticChecker checker, BlockContext ctx, SymbolTable parentTable, boolean allowEscapeStmts) {
        IrBlock block = createEmpty(parentTable, allowEscapeStmts);
        block.loadBlock(checker, ctx);
        return block;
    }

    public static IrBlock createEmpty(DecafSemanticChecker checker, boolean allowEscapeStmts) {
        List<IrStatement> statements = new ArrayList<>();
        SymbolTable parentTable = checker.currentSymbolTable();
        LocalSymbolTable symbolTable = new LocalSymbolTable(parentTable);
        return new IrBlock(symbolTable, statements, allowEscapeStmts);
    }

    public static IrBlock createEmpty(SymbolTable parentTable, boolean allowEscapeStmts) {
        List<IrStatement> statements = new ArrayList<>();
        LocalSymbolTable symbolTable = new LocalSymbolTable(parentTable);
        return new IrBlock(symbolTable, statements, allowEscapeStmts);
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
            if(!this.allowEscapeStmts) {
                if(statement instanceof BreakStmtContext || statement instanceof ContinueStmtContext) {
                    throw new ImproperEscapeError("Break and Continue can only be used inside loops", statement);
                }
            }
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
        pw.println(prefix + "{");
        for (VariableDescriptor var : symbolTable.getVariables().values()) {
            var.prettyPrint(pw, prefix + "    ");
        }
        for (IrStatement st : statements) {
            pw.println();
            st.prettyPrint(pw, prefix + "    ");
        }
        pw.println(prefix + "}");
    }
}