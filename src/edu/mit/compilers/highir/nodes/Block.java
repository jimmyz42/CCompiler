package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.cfg.CFGAble;
import edu.mit.compilers.cfg.CFGContext;
import edu.mit.compilers.grammar.DecafParser.BlockContext;
import edu.mit.compilers.grammar.DecafParser.Field_declContext;
import edu.mit.compilers.grammar.DecafParser.StatementContext;
import edu.mit.compilers.grammar.DecafParser.BreakStmtContext;
import edu.mit.compilers.grammar.DecafParser.ContinueStmtContext;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.highir.symboltable.SymbolTable;
import edu.mit.compilers.highir.symboltable.LocalSymbolTable;
import edu.mit.compilers.highir.descriptor.VariableDescriptor;

import edu.mit.compilers.cfg.CFGAble;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.cfg.components.BasicBlock;

import exceptions.SemanticError;
import exceptions.ImproperEscapeError;

public class Block extends Ir {
    private final LocalSymbolTable symbolTable;
    private List<Statement> statements;
    private boolean isLocalBlock;

    public Block(LocalSymbolTable symbolTable, List<Statement> statements, boolean isLocalBlock) {
        this.symbolTable = symbolTable;
        this.statements = statements;
        this.isLocalBlock = isLocalBlock;
    }

    public LocalSymbolTable getSymbolTable() {
        return symbolTable;
    }

    public static Block create(DecafSemanticChecker checker, BlockContext ctx, boolean isLocalBlock) {
        SymbolTable parentTable = checker.currentSymbolTable();
        return create(checker, ctx, parentTable, isLocalBlock);
    }

    public static Block create(DecafSemanticChecker checker, BlockContext ctx, SymbolTable parentTable, boolean isLocalBlock) {
        Block block = createEmpty(parentTable, isLocalBlock);
        block.loadBlock(checker, ctx);
        return block;
    }

    public static Block createEmpty(DecafSemanticChecker checker, boolean isLocalBlock) {
        List<Statement> statements = new ArrayList<>();
        SymbolTable parentTable = checker.currentSymbolTable();
        LocalSymbolTable symbolTable = new LocalSymbolTable(parentTable);
        return new Block(symbolTable, statements, isLocalBlock);
    }

    public static Block createEmpty(SymbolTable parentTable, boolean isLocalBlock) {
        List<Statement> statements = new ArrayList<>();
        LocalSymbolTable symbolTable = new LocalSymbolTable(parentTable);
        return new Block(symbolTable, statements, isLocalBlock);
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
            if(!this.isLocalBlock) {
                if(statement instanceof BreakStmtContext || statement instanceof ContinueStmtContext) {
                    throw new ImproperEscapeError("Break and Continue can only be used inside loops", statement);
                }
            }
            try {
                statements.add(Statement.create(checker, statement));
            } catch (SemanticError e) {
                checker.handleSemanticError(e);
            }
        }

        checker.popSybmolTable();
    }

    @Override
    public void prettyPrint(PrintWriter pw, String prefix) {
        pw.println(prefix + getClass().getSimpleName());
        for (VariableDescriptor var : symbolTable.getDescriptors().values()) {
            var.prettyPrint(pw, prefix + "    ");
            pw.println();
        }
        for (Statement st : statements) {
            st.prettyPrint(pw, prefix + "    ");
            pw.println();
        }
        pw.println(prefix + "end " + getClass().getSimpleName());
    }

    @Override
    public CFG generateCFG(CFGContext context) {
        ArrayList<CFGAble> components = new ArrayList<>();
        for(VariableDescriptor desc: symbolTable.getDescriptors().values()) {
            components.add(desc);
        }
        BasicBlock symbolBlock = BasicBlock.create(components);
        CFG currentCFG = symbolBlock;
        for(Statement statement: statements) {
            CFG nextCFG = statement.generateCFG(context);
            currentCFG.setNextBlock(nextCFG.getEntryBlock());
            nextCFG.setPreviousBlock(currentCFG.getExitBlock());
            currentCFG = nextCFG;
        }

        return new CFG(symbolBlock, currentCFG.getExitBlock());
    }
}