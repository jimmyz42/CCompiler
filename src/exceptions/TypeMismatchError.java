package exceptions;

import org.antlr.v4.runtime.ParserRuleContext;

public class TypeMismatchError extends SemanticError {
    public TypeMismatchError(String msg, ParserRuleContext context) {
        super(msg, context);
    }
}
