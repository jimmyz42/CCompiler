package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;
import exceptions.TypeMismatchError;

public class IrEqOpExpr extends IrBinOpExpr {
    public IrEqOpExpr(IrEqOp operator, IrExpression lhs, IrExpression rhs) {
        super(operator, lhs, rhs);
        if (lhs.getExpressionType() != rhs.getExpressionType() || !(lhs.getExpressionType() instanceof TypeScalar)) {
            throw new TypeMismatchError("Expected scalar arguments of the same type");
        }
    }

    public static IrEqOpExpr create(DecafSemanticChecker checker, DecafParser.EqOpExprContext ctx) {
        IrEqOp operator = IrEqOp.create(checker, ctx.EQ_OP());
        IrExpression lhs = IrExpression.create(checker, ctx.expr(0));
        IrExpression rhs = IrExpression.create(checker, ctx.expr(1));
        return new IrEqOpExpr(operator, lhs, rhs);
    }
    
    @Override
    public Type getExpressionType() {
        return TypeScalar.BOOL;
    }
}