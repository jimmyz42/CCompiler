package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;

public class IrAddOpExpr extends IrBinOpExpr {
    private IrBinOp operator;
    private IrExpression lhs;
    private IrExpression rhs;

    public IrAddOpExpr(IrBinOp operator, IrExpression lhs, IrExpression rhs) {
        super(operator, lhs, rhs);
    }

    public static IrAddOpExpr create(DecafSemanticChecker checker, DecafParser.AddOpExprContext ctx) {
        IrAddOp operator = IrAddOp.create(checker, ctx.PLUS_OP());
        //TODO fix this code so it adds the plus_op or dash operator depending which exists
        //IrAddOp operator = IrAddOp.create(checker, ctx.DASH());
        IrExpression lhs = IrExpression.create(checker, ctx.expr(0));
        IrExpression rhs = IrExpression.create(checker, ctx.expr(1));
        return new IrAddOpExpr(operator, lhs, rhs);
    }
}