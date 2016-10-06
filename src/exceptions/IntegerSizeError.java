package exceptions;

import org.antlr.v4.runtime.ParserRuleContext;

public class IntegerSizeError extends SemanticError {
    public IntegerSizeError(String msg, ParserRuleContext context) {
        super(msg, context);
    }
}
