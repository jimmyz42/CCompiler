package edu.mit.compilers.highir.nodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.lowir.Register;
import edu.mit.compilers.lowir.instructions.Add;
import edu.mit.compilers.lowir.instructions.Imul;
import edu.mit.compilers.lowir.instructions.Instruction;
import exceptions.TypeMismatchError;

public class MulOpExpr extends BinOpExpr {
    public MulOpExpr(BinOp operator, Expression lhs, Expression rhs) {
        super(operator, lhs, rhs);
    }

    public static MulOpExpr create(DecafSemanticChecker checker, DecafParser.MulOpExprContext ctx) {
        MulOp operator = MulOp.create(checker, ctx.MUL_OP());
        Expression lhs = Expression.create(checker, ctx.expr(0));
        Expression rhs = Expression.create(checker, ctx.expr(1));

        if (lhs.getExpressionType() != ScalarType.INT) {
            throw new TypeMismatchError("Left argument of * must be an int, got a " +
                    lhs.getExpressionType(), ctx.expr(0));
        }
        if (rhs.getExpressionType() != ScalarType.INT) {
            throw new TypeMismatchError("Right argument of * must be an int, got a " +
                    rhs.getExpressionType(), ctx.expr(1));
        }
        
        return new MulOpExpr(operator, lhs, rhs);
    }

    @Override
    public Type getExpressionType() {
        return ScalarType.INT;
    }
    
    @Override
    public List<Instruction> generateAssembly(){
    	//TODO: figure out which registers store what values 
    	Register dest = new Register(lhs);
    	Register src = new Register(rhs);
    	Instruction add = new Imul(src, dest);
    	return new ArrayList<Instruction>(Arrays.asList(add));
    }
}