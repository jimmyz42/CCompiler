package edu.mit.compilers.highir.nodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.PrintWriter;

import edu.mit.compilers.cfg.Condition;
import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.lowir.Register;
import edu.mit.compilers.lowir.instructions.Cmp;
import edu.mit.compilers.lowir.instructions.Instruction;
import exceptions.TypeMismatchError;

public class EqOpExpr extends BinOpExpr implements Condition {
    public EqOpExpr(EqOp operator, Expression lhs, Expression rhs) {
        super(operator, lhs, rhs);
    }

    public static EqOpExpr create(DecafSemanticChecker checker, DecafParser.EqOpExprContext ctx) {
        EqOp operator = EqOp.create(checker, ctx.EQ_OP());
        Expression lhs = Expression.create(checker, ctx.expr(0));
        Expression rhs = Expression.create(checker, ctx.expr(1));

        if (lhs.getExpressionType() != rhs.getExpressionType() || !(lhs.getExpressionType() instanceof ScalarType)) {
            throw new TypeMismatchError("Expected scalar arguments of the same type", ctx);
        }

        return new EqOpExpr(operator, lhs, rhs);
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
    	//TODO: operator.toString is "==" : cmove
    	//TODO: operator.toString is "!=" : cmovne
    	Register dest = new Register(lhs);
    	Register src = new Register(rhs);
    	Instruction cmp = new Cmp(src, dest);
    	return new ArrayList<Instruction>(Arrays.asList(cmp));
    }
}