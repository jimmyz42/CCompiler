package exceptions;

public class BadArraySizeError extends SemanticError {
    public BadArraySizeError(String msg) {
        super(msg);
    }

    public BadArraySizeError(String msg, Throwable cause) {
        super(msg, cause);
    }
}
