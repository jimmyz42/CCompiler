package exceptions;

public class MethodCallException extends SemanticError {
    public MethodCallException(String msg) {
        super(msg);
    }
    
    public MethodCallException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
