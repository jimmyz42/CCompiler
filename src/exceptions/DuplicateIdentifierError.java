package exceptions;

public class DuplicateIdentifierError extends SemanticError {
    public DuplicateIdentifierError(String msg) {
        super(msg);
    }
    
    public DuplicateIdentifierError(String msg, Throwable cause) {
        super(msg, cause);
    }
}
