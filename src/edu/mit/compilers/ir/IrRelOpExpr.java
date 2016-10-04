package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;
import exceptions.TypeMismatchException;

public class IrRelOpExpr extends IrBinOpExpr {
    public IrRelOpExpr(IrBinOp operator, IrExpression lhs, IrExpression rhs) {
        super(operator, lhs, rhs);
        if (lhs.getExpressionType() != TypeScalar.INT || rhs.getExpressionType() != TypeScalar.INT) {
            throw new TypeMismatchException("Expected int arguments");
        }
    }

    public static IrRelOpExpr create(DecafSemanticChecker checker, DecafParser.RelOpExprContext ctx) {
        IrRelOp operator = IrRelOp.create(checker, ctx.REL_OP());
        IrExpression lhs = IrExpression.create(checker, ctx.expr(0));
        IrExpression rhs = IrExpression.create(checker, ctx.expr(1));
        return new IrRelOpExpr(operator, lhs, rhs);
    }
    
    @Override
    public Type getExpressionType() {
        return TypeScalar.BOOL;
    }
}