package exceptions;

public class TypeMismatchException extends RuntimeException {
    // TODO you should be able to get line/pos of the decaf source at which the error occurred
    public TypeMismatchException(String msg) {
        super(msg);
    }
    
    public TypeMismatchException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
