package exceptions;

import org.antlr.v4.runtime.ParserRuleContext;

public class SemanticError extends RuntimeException {
    private final ParserRuleContext context;
    
    public SemanticError(String msg, ParserRuleContext context) {
        super(msg);
        this.context = context;
    }
    
    @Override
    public String toString() {
        // This could possibly lead to a misleading error message if a token spans more than 1 line
        // I don't think this can ever happen in our grammar though.
        int line1 = context.getStart().getLine();
        int pos1 = context.getStart().getCharPositionInLine();
        int line2 = context.getStop().getLine();
        int pos2 = context.getStop().getCharPositionInLine() + context.getStop().getText().length();
        return String.format("%4d:%-3d to %4d:%-3d  %s", line1, pos1, line2, pos2, getMessage());
    }
}
