package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.mit.compilers.cfg.Condition;
import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.lowir.Register;
import edu.mit.compilers.lowir.instructions.Add;
import edu.mit.compilers.lowir.instructions.Cmp;
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
    public List<Instruction> generateAssembly(){
    	//TODO: figure out which registers store what values 
    	//depending on the operation: 
    	//RelOp.toString is "==" : cmove
    	//RelOp.toString is ">": cmovg
    	//RelOp.toString is "<": cmovl
    	//RelOp.toString is ">=": cmovge
    	//RelOp.toString is "<=": cmovle
    	Register dest = new Register(lhs);
    	Register src = new Register(rhs);
    	Instruction cmp = new Cmp(src, dest);
    	return new ArrayList<Instruction>(Arrays.asList(cmp));
    }
}