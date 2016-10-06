package exceptions;

import org.antlr.v4.runtime.ParserRuleContext;

public class BadArraySizeError extends SemanticError {
    public BadArraySizeError(String msg, ParserRuleContext context) {
        super(msg, context);
    }
}
