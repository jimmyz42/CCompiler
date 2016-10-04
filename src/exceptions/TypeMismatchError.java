package exceptions;

public class TypeMismatchError extends SemanticError {
    public TypeMismatchError(String msg) {
        super(msg);
    }
    
    public TypeMismatchError(String msg, Throwable cause) {
        super(msg, cause);
    }
}
