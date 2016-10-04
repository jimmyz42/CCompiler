package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;
import exceptions.TypeMismatchError;

public class IrAndOpExpr extends IrLogicalOpExpr {
    public IrAndOpExpr(IrBinOp operator, IrExpression lhs, IrExpression rhs) {
        super(operator, lhs, rhs);
    }

    public static IrAndOpExpr create(DecafSemanticChecker checker, DecafParser.AndOpExprContext ctx) {
        IrAndOp operator = IrAndOp.create(checker, ctx.AND_OP());
        IrExpression lhs = IrExpression.create(checker, ctx.expr(0));
        IrExpression rhs = IrExpression.create(checker, ctx.expr(1));
        return new IrAndOpExpr(operator, lhs, rhs);
    }
}