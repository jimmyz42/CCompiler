package exceptions;

public class UndeclaredVariableException extends RuntimeException {
    // TODO you should be able to get line/pos
    public UndeclaredVariableException(String msg) {
        super(msg);
    }
    
    public UndeclaredVariableException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
