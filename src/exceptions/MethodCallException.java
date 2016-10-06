package exceptions;

import org.antlr.v4.runtime.ParserRuleContext;

public class MethodCallException extends SemanticError {
    public MethodCallException(String msg, ParserRuleContext context) {
        super(msg, context);
    }
}
