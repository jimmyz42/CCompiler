package exceptions;

import org.antlr.v4.runtime.ParserRuleContext;

public class UndeclaredIdentifierError extends SemanticError {
    public UndeclaredIdentifierError(String msg, ParserRuleContext context) {
        super(msg, context);
    }
}
