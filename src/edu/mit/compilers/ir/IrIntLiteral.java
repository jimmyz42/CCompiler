package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;
import exceptions.IntegerSizeError;
import org.antlr.v4.runtime.ParserRuleContext;

class IrIntLiteral extends IrLiteral {
    private long terminal;

    public IrIntLiteral(long terminal) {
        this.terminal = terminal;
    }

    public static IrIntLiteral create(DecafSemanticChecker checker, DecafParser.IntLiteralContext ctx) {
         String text = ctx.INT_LITERAL().getText();
         Long terminal = parseLong(text, ctx);
         return new IrIntLiteral(terminal);
    }

    public static long parseLong(String text, ParserRuleContext ctx) {
        try {
            return Long.decode(text.replace("ll", ""));
        }
        catch (NumberFormatException e) {
            throw new IntegerSizeError("Integer Values must be at most 64 bits", ctx);
        }
    }

    @Override
    public Type getExpressionType() {
        return TypeScalar.INT;
    }

    @Override
    public String toString() {
        return Long.toString(terminal);
    }
}