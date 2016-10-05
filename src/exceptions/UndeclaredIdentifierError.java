package exceptions;

public class UndeclaredIdentifierError extends SemanticError {
    public UndeclaredIdentifierError(String msg) {
        super(msg);
    }
    
    public UndeclaredIdentifierError(String msg, Throwable cause) {
        super(msg, cause);
    }
}
