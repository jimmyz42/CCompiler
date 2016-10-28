package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;
import java.util.List;
import java.util.Collections;

import edu.mit.compilers.cfg.CFGContext;
import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.instructions.Instruction;
import edu.mit.compilers.lowir.instructions.Ret;
import exceptions.TypeMismatchError;

public class ReturnStmt extends Statement {
    private final Expression expression;

    public ReturnStmt(Expression expression) {
        this.expression = expression;
    }

    public static ReturnStmt create(DecafSemanticChecker checker, DecafParser.ReturnStmtContext ctx) {
        Type methodType = checker.currentMethodDescriptor().getType();
        if(ctx.expr() == null) {
            if(!(methodType instanceof VoidType)) {
                throw new TypeMismatchError("Empty return value are only allowed in void methods", ctx);
            }
            return new ReturnStmt(null);
        }

        Expression expression = Expression.create(checker, ctx.expr());
        if(expression.getExpressionType() != methodType) {
            throw new TypeMismatchError("Return type " + expression.getExpressionType() + " does not match the expected type " + methodType, ctx);
        }
        return new ReturnStmt(expression);
    }

    @Override
    public void prettyPrint(PrintWriter pw, String prefix) {
        super.prettyPrint(pw, prefix);
        pw.println(prefix + "-return:");
        if(expression != null) {
            expression.prettyPrint(pw, prefix + "    ");
        }
    }

    @Override
    public void cfgPrint(PrintWriter pw, String prefix) {
        pw.println(prefix + "return ");
        expression.cfgPrint(pw, prefix);
    }

    @Override
    public CFG generateCFG(CFGContext context) {
    	BasicBlock block = BasicBlock.createEmpty("return");
    	BasicBlock methodEnd = context.currentMethodCFG().getExitBlock();
    	block.setNextBlock(methodEnd);
    	methodEnd.addPreviousBlock(block);
    	return block;
    }

    @Override
    public void generateAssembly(AssemblyContext ctx) {
        ctx.addInstruction(Ret.create());
    }
}