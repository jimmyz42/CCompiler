package edu.mit.compilers.ir;

public enum Type {
    VOID(false, false),
    INT(true, false),
    BOOL(true, false),
    INT_ARRAY(false, true),
    BOOL_ARRAY(false, true);
    
    public boolean isScalar;
    public boolean isVector;
    
    private Type(boolean isScalar, boolean isVector) {
        this.isScalar = isScalar;
        this.isVector = isVector;
    }

    boolean isScalar() {
        return isScalar;
    }
    
    boolean isVector() {
        return isVector;
    }
    
    boolean isVoid() {
        return !isScalar && !isVector;
    }
}
