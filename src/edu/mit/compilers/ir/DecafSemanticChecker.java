// Generated from /home/firescar/Documents/MIT/6.035/nchinda26035/src/edu/mit/compilers/grammar/Decaf.g4 by ANTLR 4.5.3

  package edu.mit.compilers.ir;

import java.util.Stack;

import edu.mit.compilers.grammar.*;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

/**
 * This class provides an empty implementation of {@link DecafVisitor},
 * which can be extended to create a visitor which only needs to handle a subset
 * of the available methods.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public class DecafSemanticChecker extends DecafBaseVisitor<Object> {

    // What's this used for?
    ParseTreeProperty<Object> values = new ParseTreeProperty<Object>();

    private void setValue(ParseTree node, Object value) { values.put(node, value); }
    private Object getValue(ParseTree node) { return values.get(node); }
    
    private Stack<SymbolTable> symbolTables = new Stack<>();
    public SymbolTable currentSymbolTable() {
        return symbolTables.peek();
    }
    public SymbolTable popSybmolTable() {
        return symbolTables.pop();
    }
    public void pushSymbolTable(SymbolTable symbolTable) {
        symbolTables.push(symbolTable);
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public Object visitProgram(DecafParser.ProgramContext ctx) { return IrProgram.create(this, ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public Object visitExtern_decl(DecafParser.Extern_declContext ctx) { return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public Object visitMethod_decl(DecafParser.Method_declContext ctx) { return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public Object visitBlock(DecafParser.BlockContext ctx) { return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public Object visitField_decl(DecafParser.Field_declContext ctx) { return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public Object visitField(DecafParser.FieldContext ctx) { return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public Object visitType(DecafParser.TypeContext ctx) { return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public IrIfStmt visitIfStmt(DecafParser.IfStmtContext ctx) { return IrIfStmt.create(this, ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public IrForStmt visitForStmt(DecafParser.ForStmtContext ctx) { return IrForStmt.create(this, ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public IrWhileStmt visitWhileStmt(DecafParser.WhileStmtContext ctx) { return IrWhileStmt.create(this, ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public IrReturnStmt visitReturnStmt(DecafParser.ReturnStmtContext ctx) { return IrReturnStmt.create(this, ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public IrBreakStmt visitBreakStmt(DecafParser.BreakStmtContext ctx) { return IrBreakStmt.create(this, ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public IrContStmt visitContinueStmt(DecafParser.ContinueStmtContext ctx) { return IrContStmt.create(this, ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public Object visitMethodCallStmt(DecafParser.MethodCallStmtContext ctx) { return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public IrAssignStmt visitAssignStmt(DecafParser.AssignStmtContext ctx) { return IrAssignStmt.create(this, ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public Object visitMethod_call(DecafParser.Method_callContext ctx) { return visitChildren(ctx); }
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public Object visitArrayLocation(DecafParser.ArrayLocationContext ctx) { return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public Object visitIdLocation(DecafParser.IdLocationContext ctx) { return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public IrMulOpExpr visitMulOpExpr(DecafParser.MulOpExprContext ctx) { return IrMulOpExpr.create(this, ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public IrLogicalOpExpr visitAndOpExpr(DecafParser.AndOpExprContext ctx) { return IrAndOpExpr.create(this, ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public IrLocation visitLocationExpr(DecafParser.LocationExprContext ctx) { return IrLocation.create(this, ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public IrRelOpExpr visitRelOpExpr(DecafParser.RelOpExprContext ctx) { return IrRelOpExpr.create(this, ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public IrNegExpr visitNegExpr(DecafParser.NegExprContext ctx) { return IrNegExpr.create(this, ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public IrSizeofTypeExpr visitSizeofTypeExpr(DecafParser.SizeofTypeExprContext ctx) { return IrSizeofTypeExpr.create(this, ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public IrOrOpExpr visitOrOpExpr(DecafParser.OrOpExprContext ctx) { return IrOrOpExpr.create(this, ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public IrExpression visitGroupExpr(DecafParser.GroupExprContext ctx) { return IrExpression.create(this, ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public IrAddOpExpr visitAddOpExpr(DecafParser.AddOpExprContext ctx) { return IrAddOpExpr.create(this, ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public IrEqOpExpr visitEqOpExpr(DecafParser.EqOpExprContext ctx) { return IrEqOpExpr.create(this, ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public IrLiteral visitLiteralExpr(DecafParser.LiteralExprContext ctx) { return IrLiteral.create(this, ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public IrCallExpr visitCallExpr(DecafParser.CallExprContext ctx) { return IrCallExpr.create(this, ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public IrNotExpr visitNotExpr(DecafParser.NotExprContext ctx) { return IrNotExpr.create(this, ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public IrSizeofIdExpr visitSizeofIdExpr(DecafParser.SizeofIdExprContext ctx) { return IrSizeofIdExpr.create(this, ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override public IrIntLiteral visitIntLiteral(DecafParser.IntLiteralContext ctx) { return IrIntLiteral.create(this, ctx); }
 	/**
 	 * {@inheritDoc}
 	 *
 	 * <p>The default implementation returns the result of calling
 	 * {@link #visitChildren} on {@code ctx}.</p>
 	 */
 	@Override public IrCharLiteral visitCharLiteral(DecafParser.CharLiteralContext ctx) { return IrCharLiteral.create(this, ctx); }
 	/**
 	 * {@inheritDoc}
 	 *
 	 * <p>The default implementation returns the result of calling
 	 * {@link #visitChildren} on {@code ctx}.</p>
 	 */
 	@Override public IrBoolLiteral visitBoolLiteral(DecafParser.BoolLiteralContext ctx) { return IrBoolLiteral.create(this, ctx); }

    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public Object visitExtern_arg(DecafParser.Extern_argContext ctx) { return visitChildren(ctx); }

    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public Object visitAssign_op(DecafParser.Assign_opContext ctx) { return visitChildren(ctx); }
}