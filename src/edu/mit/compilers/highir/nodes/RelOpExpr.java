package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import edu.mit.compilers.cfg.Condition;
import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.lowir.instructions.Instruction;
import exceptions.TypeMismatchError;

public class RelOpExpr extends BinOpExpr implements Condition {
    public RelOpExpr(BinOp operator, Expression lhs, Expression rhs) {
        super(operator, lhs, rhs);
    }

    public static RelOpExpr create(DecafSemanticChecker checker, DecafParser.RelOpExprContext ctx) {
        RelOp operator = RelOp.create(checker, ctx.REL_OP());
        Expression lhs = Expression.create(checker, ctx.expr(0));
        Expression rhs = Expression.create(checker, ctx.expr(1));

        if (lhs.getExpressionType() != ScalarType.INT) {
            throw new TypeMismatchError("Left argument of " + operator + " must be an int, got a " +
                    lhs.getExpressionType(), ctx.expr(0));
        }
        if (rhs.getExpressionType() != ScalarType.INT) {
            throw new TypeMismatchError("Right argument of " + operator + " must be an int, got a " +
                    rhs.getExpressionType(), ctx.expr(1));
        }

        return new RelOpExpr(operator, lhs, rhs);
    }

    @Override
    public Type getExpressionType() {
        return ScalarType.BOOL;
    }
    
    @Override
    public BasicBlock shortCircuit(CFG trueBranch, CFG falseBranch) {
    	BasicBlock block = BasicBlock.createWithCondition(this);
    	block.setNextBlocks(Arrays.asList(trueBranch.getEntryBlock(), falseBranch.getEntryBlock()));
    	trueBranch.setPreviousBlock(block);
    	falseBranch.setPreviousBlock(block);
    	return block;
    }

	@Override
	public List<Instruction> generateAssembly() {
		// TODO Auto-generated method stub
		return null;
	}
}