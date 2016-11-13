// Generated from /home/firescar/Documents/MIT/6.035/nchinda26035/src/edu/mit/compilers/grammar/Decaf.g4 by ANTLR 4.5.3

package edu.mit.compilers.highir;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import edu.mit.compilers.grammar.DecafBaseVisitor;
import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.grammar.DecafVisitor;
import edu.mit.compilers.highir.descriptor.MethodDescriptor;
import edu.mit.compilers.highir.nodes.AddOpExpr;
import edu.mit.compilers.highir.nodes.AndOpExpr;
import edu.mit.compilers.highir.nodes.ArrayLocation;
import edu.mit.compilers.highir.nodes.AssignStmt;
import edu.mit.compilers.highir.nodes.BoolLiteral;
import edu.mit.compilers.highir.nodes.BreakStmt;
import edu.mit.compilers.highir.nodes.CharLiteral;
import edu.mit.compilers.highir.nodes.ContStmt;
import edu.mit.compilers.highir.nodes.EqOpExpr;
import edu.mit.compilers.highir.nodes.Expression;
import edu.mit.compilers.highir.nodes.ForStmt;
import edu.mit.compilers.highir.nodes.IdLocation;
import edu.mit.compilers.highir.nodes.IfStmt;
import edu.mit.compilers.highir.nodes.IntLiteral;
import edu.mit.compilers.highir.nodes.Literal;
import edu.mit.compilers.highir.nodes.MethodCallExpr;
import edu.mit.compilers.highir.nodes.MethodCallStmt;
import edu.mit.compilers.highir.nodes.MulOpExpr;
import edu.mit.compilers.highir.nodes.NegExpr;
import edu.mit.compilers.highir.nodes.NotExpr;
import edu.mit.compilers.highir.nodes.OrOpExpr;
import edu.mit.compilers.highir.nodes.Program;
import edu.mit.compilers.highir.nodes.RelOpExpr;
import edu.mit.compilers.highir.nodes.ReturnStmt;
import edu.mit.compilers.highir.nodes.SizeofIdExpr;
import edu.mit.compilers.highir.nodes.SizeofTypeExpr;
import edu.mit.compilers.highir.nodes.WhileStmt;
import edu.mit.compilers.highir.symboltable.SymbolTable;
import exceptions.SemanticError;

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

    private Stack<MethodDescriptor> methodDescriptors = new Stack<>();
    public MethodDescriptor currentMethodDescriptor() {
        return methodDescriptors.peek();
    }
    public MethodDescriptor popMethodDescriptor() {
        return methodDescriptors.pop();
    }
    public void pushMethodDescriptor(MethodDescriptor methodDescriptor) {
        methodDescriptors.push(methodDescriptor);
    }

    private List<SemanticError> errors = new ArrayList<>();
    public void handleSemanticError(SemanticError e) {
        errors.add(e);
    }
    public List<SemanticError> getSemanticErrors() {
        return Collections.unmodifiableList(errors);
    }

    /**
    * {@inheritDoc}
    *
    * <p>The default implementation returns the result of calling
    * {@link #visitChildren} on {@code ctx}.</p>
    */
    @Override public Object visitProgram(DecafParser.ProgramContext ctx) { return Program.create(this, ctx); }
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
    @Override public IfStmt visitIfStmt(DecafParser.IfStmtContext ctx) { return IfStmt.create(this, ctx); }
    /**
    * {@inheritDoc}
    *
    * <p>The default implementation returns the result of calling
    * {@link #visitChildren} on {@code ctx}.</p>
    */
    @Override public ForStmt visitForStmt(DecafParser.ForStmtContext ctx) { return ForStmt.create(this, ctx); }
    /**
    * {@inheritDoc}
    *
    * <p>The default implementation returns the result of calling
    * {@link #visitChildren} on {@code ctx}.</p>
    */
    @Override public WhileStmt visitWhileStmt(DecafParser.WhileStmtContext ctx) { return WhileStmt.create(this, ctx); }
    /**
    * {@inheritDoc}
    *
    * <p>The default implementation returns the result of calling
    * {@link #visitChildren} on {@code ctx}.</p>
    */
    @Override public ReturnStmt visitReturnStmt(DecafParser.ReturnStmtContext ctx) { return ReturnStmt.create(this, ctx); }
    /**
    * {@inheritDoc}
    *
    * <p>The default implementation returns the result of calling
    * {@link #visitChildren} on {@code ctx}.</p>
    */
    @Override public BreakStmt visitBreakStmt(DecafParser.BreakStmtContext ctx) { return BreakStmt.create(this, ctx); }
    /**
    * {@inheritDoc}
    *
    * <p>The default implementation returns the result of calling
    * {@link #visitChildren} on {@code ctx}.</p>
    */
    @Override public ContStmt visitContinueStmt(DecafParser.ContinueStmtContext ctx) { return ContStmt.create(this, ctx); }
    /**
    * {@inheritDoc}
    *
    * <p>The default implementation returns the result of calling
    * {@link #visitChildren} on {@code ctx}.</p>
    */
    @Override public MethodCallStmt visitMethodCallStmt(DecafParser.MethodCallStmtContext ctx) { return MethodCallStmt.create(this, ctx); }
    /**
    * {@inheritDoc}
    *
    * <p>The default implementation returns the result of calling
    * {@link #visitChildren} on {@code ctx}.</p>
    */
    @Override public AssignStmt visitAssignStmt(DecafParser.AssignStmtContext ctx) { return AssignStmt.create(this, ctx); }
    /**
    * {@inheritDoc}
    *
    * <p>The default implementation returns the result of calling
    * {@link #visitChildren} on {@code ctx}.</p>
    */
    @Override public MethodCallExpr visitMethod_call(DecafParser.Method_callContext ctx) { return MethodCallExpr.create(this, ctx); }
    /**
    * {@inheritDoc}
    *
    * <p>The default implementation returns the result of calling
    * {@link #visitChildren} on {@code ctx}.</p>
    */
    @Override public ArrayLocation visitArrayLocation(DecafParser.ArrayLocationContext ctx) { return ArrayLocation.create(this, ctx); }
    /**
    * {@inheritDoc}
    *
    * <p>The default implementation returns the result of calling
    * {@link #visitChildren} on {@code ctx}.</p>
    */
    @Override public IdLocation visitIdLocation(DecafParser.IdLocationContext ctx) { return IdLocation.create(this, ctx); }
    /**
    * {@inheritDoc}
    *
    * <p>The default implementation returns the result of calling
    * {@link #visitChildren} on {@code ctx}.</p>
    */
    @Override public MulOpExpr visitMulOpExpr(DecafParser.MulOpExprContext ctx) { return MulOpExpr.create(this, ctx); }
    /**
    * {@inheritDoc}
    *
    * <p>The default implementation returns the result of calling
    * {@link #visitChildren} on {@code ctx}.</p>
    */
    @Override public AndOpExpr visitAndOpExpr(DecafParser.AndOpExprContext ctx) { return AndOpExpr.create(this, ctx); }
    /**
    * {@inheritDoc}
    *
    * <p>The default implementation returns the result of calling
    * {@link #visitChildren} on {@code ctx}.</p>
    */
    @Override public Object visitLocationExpr(DecafParser.LocationExprContext ctx) { return visitChildren(ctx); }
    /**
    * {@inheritDoc}
    *
    * <p>The default implementation returns the result of calling
    * {@link #visitChildren} on {@code ctx}.</p>
    */
    @Override public RelOpExpr visitRelOpExpr(DecafParser.RelOpExprContext ctx) { return RelOpExpr.create(this, ctx); }
    /**
    * {@inheritDoc}
    *
    * <p>The default implementation returns the result of calling
    * {@link #visitChildren} on {@code ctx}.</p>
    */
    @Override public NegExpr visitNegExpr(DecafParser.NegExprContext ctx) { return NegExpr.create(this, ctx); }
    /**
    * {@inheritDoc}
    *
    * <p>The default implementation returns the result of calling
    * {@link #visitChildren} on {@code ctx}.</p>
    */
    @Override public SizeofTypeExpr visitSizeofTypeExpr(DecafParser.SizeofTypeExprContext ctx) { return SizeofTypeExpr.create(this, ctx); }
    /**
    * {@inheritDoc}
    *
    * <p>The default implementation returns the result of calling
    * {@link #visitChildren} on {@code ctx}.</p>
    */
    @Override public OrOpExpr visitOrOpExpr(DecafParser.OrOpExprContext ctx) { return OrOpExpr.create(this, ctx); }
    /**
    * {@inheritDoc}
    *
    * <p>The default implementation returns the result of calling
    * {@link #visitChildren} on {@code ctx}.</p>
    */
    @Override public Expression visitGroupExpr(DecafParser.GroupExprContext ctx) { return Expression.create(this, ctx.expr()); }
    /**
    * {@inheritDoc}
    *
    * <p>The default implementation returns the result of calling
    * {@link #visitChildren} on {@code ctx}.</p>
    */
    @Override public AddOpExpr visitAddOpExpr(DecafParser.AddOpExprContext ctx) { return AddOpExpr.create(this, ctx); }
    /**
    * {@inheritDoc}
    *
    * <p>The default implementation returns the result of calling
    * {@link #visitChildren} on {@code ctx}.</p>
    */
    @Override public EqOpExpr visitEqOpExpr(DecafParser.EqOpExprContext ctx) { return EqOpExpr.create(this, ctx); }
    /**
    * {@inheritDoc}
    *
    * <p>The default implementation returns the result of calling
    * {@link #visitChildren} on {@code ctx}.</p>
    */
    @Override public Literal visitLiteralExpr(DecafParser.LiteralExprContext ctx) { return Literal.create(this, ctx.literal()); }
    /**
    * {@inheritDoc}
    *
    * <p>The default implementation returns the result of calling
    * {@link #visitChildren} on {@code ctx}.</p>
    */
    @Override public MethodCallExpr visitCallExpr(DecafParser.CallExprContext ctx) { return visitMethod_call(ctx.method_call()); }
    /**
    * {@inheritDoc}
    *
    * <p>The default implementation returns the result of calling
    * {@link #visitChildren} on {@code ctx}.</p>
    */
    @Override public NotExpr visitNotExpr(DecafParser.NotExprContext ctx) { return NotExpr.create(this, ctx); }
    /**
    * {@inheritDoc}
    *
    * <p>The default implementation returns the result of calling
    * {@link #visitChildren} on {@code ctx}.</p>
    */
    @Override public SizeofIdExpr visitSizeofIdExpr(DecafParser.SizeofIdExprContext ctx) { return SizeofIdExpr.create(this, ctx); }
    /**
    * {@inheritDoc}
    *
    * <p>The default implementation returns the result of calling
    * {@link #visitChildren} on {@code ctx}.</p>
    */
    @Override public IntLiteral visitIntLiteral(DecafParser.IntLiteralContext ctx) { return IntLiteral.create(this, ctx); }
    /**
    * {@inheritDoc}
    *
    * <p>The default implementation returns the result of calling
    * {@link #visitChildren} on {@code ctx}.</p>
    */
    @Override public CharLiteral visitCharLiteral(DecafParser.CharLiteralContext ctx) { return CharLiteral.create(this, ctx); }
    /**
    * {@inheritDoc}
    *
    * <p>The default implementation returns the result of calling
    * {@link #visitChildren} on {@code ctx}.</p>
    */
    @Override public BoolLiteral visitBoolLiteral(DecafParser.BoolLiteralContext ctx) { return BoolLiteral.create(this, ctx); }

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