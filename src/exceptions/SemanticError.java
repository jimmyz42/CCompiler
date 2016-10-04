package exceptions;

public class SemanticError extends RuntimeException {
    // TODO you should be able to get line/pos of the decaf source at which the error occurred
    public SemanticError(String msg) {
        super(msg);
    }
    
    public SemanticError(String msg, Throwable cause) {
        super(msg, cause);
    }
}
