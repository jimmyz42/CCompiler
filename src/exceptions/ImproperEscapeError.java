package exceptions;

import org.antlr.v4.runtime.ParserRuleContext;

public class ImproperEscapeError extends SemanticError {
    public ImproperEscapeError(String msg, ParserRuleContext context) {
        super(msg, context);
    }
}
