package edu.mit.compilers.ir;

import java.io.PrintWriter;

import edu.mit.compilers.grammar.DecafParser;
import exceptions.TypeMismatchError;

class IrIfStmt extends IrStatement {
    private IrExpression condition;
    private IrBlock block;
    private IrBlock elseBlock;

    public IrIfStmt(IrExpression condition, IrBlock block, IrBlock elseBlock) {
        this.condition = condition;
        this.block = block;
        this.elseBlock = elseBlock;
    }

    public static IrIfStmt create(DecafSemanticChecker checker, DecafParser.IfStmtContext ctx) {
        IrExpression condition = IrExpression.create(checker, ctx.expr());
        if (condition.getExpressionType() != TypeScalar.BOOL) {
            throw new TypeMismatchError("If statement condition must be a bool");
        }
        
        IrBlock block = IrBlock.create(checker, ctx.block(0));
        IrBlock elseBlock;
        if (ctx.block().size() > 1) {
            elseBlock = IrBlock.create(checker, ctx.block(1));
        } else {
            elseBlock = IrBlock.createEmpty(checker);
        }

        return new IrIfStmt(condition, block, elseBlock);
    }
    
    @Override
    public void prettyPrint(PrintWriter pw, String prefix) {
    	pw.println(prefix + getClass().getSimpleName());
    	pw.println(prefix + "-condition:");
    	condition.prettyPrint(pw, prefix+"    ");
    	pw.println("\n" + prefix + "-IfBlock:");
        block.prettyPrint(pw, prefix + "    ");
    	pw.println(prefix + "-ElseBlock:");
        elseBlock.prettyPrint(pw, prefix + "    ");
        
    	pw.println(prefix + "end " + getClass().getSimpleName());
    }
}