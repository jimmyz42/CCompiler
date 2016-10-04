package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.grammar.DecafParser.AssignStmtContext;
import edu.mit.compilers.grammar.DecafParser.BreakStmtContext;
import edu.mit.compilers.grammar.DecafParser.ContinueStmtContext;
import edu.mit.compilers.grammar.DecafParser.ForStmtContext;
import edu.mit.compilers.grammar.DecafParser.IfStmtContext;
import edu.mit.compilers.grammar.DecafParser.ReturnStmtContext;
import edu.mit.compilers.grammar.DecafParser.WhileStmtContext;

abstract class IrStatement extends Ir {
    public static IrStatement create(DecafSemanticChecker checker, DecafParser.StatementContext ctx, SymbolTable symbolTable) {
        // I really don't like this giant block of ifs. Any better ideas? 
        if (ctx instanceof AssignStmtContext) return IrAssignStmt.create(checker, ctx, symbolTable);
        if (ctx instanceof BreakStmtContext) return IrBreakStmt.create(checker, ctx, symbolTable);
        if (ctx instanceof ContinueStmtContext) return IrContStmt.create(checker, ctx, symbolTable);
        if (ctx instanceof ForStmtContext) return IrForStmt.create(checker, ctx, symbolTable);
        if (ctx instanceof IfStmtContext) return IrIfStmt.create(checker, ctx, symbolTable);
        //if (ctx instanceof MethodCallStmtContext) return ;
        if (ctx instanceof ReturnStmtContext) return IrReturnStmt.create(checker, ctx, symbolTable);
        if (ctx instanceof WhileStmtContext) return IrWhileStmt.create(checker, ctx, symbolTable);
        throw new RuntimeException("Unknown statement type: " + ctx.getClass().getName());
    }
}