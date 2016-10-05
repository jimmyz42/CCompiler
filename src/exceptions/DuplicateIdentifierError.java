package exceptions;

import org.antlr.v4.runtime.ParserRuleContext;

public class DuplicateIdentifierError extends SemanticError {
    public DuplicateIdentifierError(String msg, ParserRuleContext context) {
        super(msg, context);
    }
}
