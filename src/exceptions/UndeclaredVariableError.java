package exceptions;

public class UndeclaredVariableError extends SemanticError {
    public UndeclaredVariableError(String msg) {
        super(msg);
    }
    
    public UndeclaredVariableError(String msg, Throwable cause) {
        super(msg, cause);
    }
}
