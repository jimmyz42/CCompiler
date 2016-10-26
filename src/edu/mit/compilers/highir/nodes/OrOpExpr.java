package edu.mit.compilers.highir.nodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.lowir.Register;
import edu.mit.compilers.lowir.instructions.Add;
import edu.mit.compilers.lowir.instructions.Instruction;
import edu.mit.compilers.lowir.instructions.Or;
import exceptions.TypeMismatchError;

public class OrOpExpr extends BinOpExpr {
    public OrOpExpr(BinOp operator, Expression lhs, Expression rhs) {
        super(operator, lhs, rhs);
    }

    public static OrOpExpr create(DecafSemanticChecker checker, DecafParser.OrOpExprContext ctx) {
        OrOp operator = OrOp.create(checker, ctx.OR_OP());
        Expression lhs = Expression.create(checker, ctx.expr(0));
        Expression rhs = Expression.create(checker, ctx.expr(1));

        if (lhs.getExpressionType() != ScalarType.BOOL) {
            throw new TypeMismatchError("Left argument of || must be an bool, got a " +
                    lhs.getExpressionType(), ctx.expr(0));
        }
        if (rhs.getExpressionType() != ScalarType.BOOL) {
            throw new TypeMismatchError("Right argument of || must be an bool, got a " +
                    rhs.getExpressionType(), ctx.expr(1));
        }
        
        return new OrOpExpr(operator, lhs, rhs);
    }

    @Override
    public Type getExpressionType() {
        return ScalarType.BOOL;
    }
    
    @Override
    public BasicBlock shortCircuit(CFG trueBranch, CFG falseBranch) {
    	CFG temp = rhs.shortCircuit(trueBranch, falseBranch);
    	return lhs.shortCircuit(trueBranch, temp);
    }
    
    @Override
    public List<Instruction> generateAssembly(){
    	//TODO: figure out which registers store what values 
    	//TODO: if complicated, figure out how to simplify 
    	Register dest = new Register(lhs);
    	Register src = new Register(rhs);
    	Instruction add = new Or(src, dest);
    	return new ArrayList<Instruction>(Arrays.asList(add));
    }
}