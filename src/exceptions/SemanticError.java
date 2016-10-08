package exceptions;

import org.antlr.v4.runtime.ParserRuleContext;

public class SemanticError extends RuntimeException {
    private final ParserRuleContext ctx;

    public SemanticError(String msg, ParserRuleContext ctx) {
        super(msg);
        this.ctx = ctx;
    }

    @Override
    public String toString() {
        // This could possibly lead to a misleading error message if a token spans more than 1 line
        // I don't think this can ever happen in our grammar though.
        int line1 = ctx.getStart().getLine();
        int pos1 = ctx.getStart().getCharPositionInLine();
        if (ctx.getStop() != null) {
            int line2 = ctx.getStop().getLine();
            int pos2 = ctx.getStop().getCharPositionInLine() + ctx.getStop().getText().length();
            return String.format("%4d:%-3d to %4d:%-3d  %s", line1, pos1, line2, pos2, getMessage());
        }
        else {
            return String.format("%4d:%-3d  %s", line1, pos1, getMessage());
        }
    }
}
