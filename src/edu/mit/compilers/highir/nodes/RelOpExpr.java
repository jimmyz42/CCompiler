package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;

import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import exceptions.TypeMismatchError;

public class RelOpExpr extends BinOpExpr {
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
    
}