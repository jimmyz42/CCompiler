package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Collections;

import edu.mit.compilers.cfg.CFGContext;
import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.Memory;
import edu.mit.compilers.lowir.Register;
import edu.mit.compilers.lowir.Storage;
import edu.mit.compilers.lowir.instructions.Instruction;
import edu.mit.compilers.lowir.instructions.Leave;
import edu.mit.compilers.lowir.instructions.Mov;
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

    public static ReturnStmt create() {
        return new ReturnStmt(null);
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
        pw.print(prefix + "return ");
        if(expression != null) {
        	expression.cfgPrint(pw, prefix);
        }
        pw.println();
    }

    @Override
    public CFG generateCFG(CFGContext context) {
        BasicBlock block = BasicBlock.create(this);
        BasicBlock methodEnd = context.currentMethodCFG().getExitBlock();
        block.setNextBlock(methodEnd);
        methodEnd.addPreviousBlock(block);
        return block;
    }

    @Override
    public void generateAssembly(AssemblyContext ctx) {
        if(expression != null) {
			expression.generateAssembly(ctx);
        	Storage returnValue = expression.getLocation(ctx);
        	ctx.addInstruction(Mov.create(returnValue, Register.create("%rax")));
        }

        ctx.addInstruction(Leave.create());
        ctx.addInstruction(Ret.create());
    }
}
