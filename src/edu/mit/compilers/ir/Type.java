package edu.mit.compilers.ir;

public enum Type {
    VOID(false, null),
    INT(true, null),
    BOOL(true, null),
    INT_ARRAY(false, INT),
    BOOL_ARRAY(false, BOOL);
    
    public boolean isScalar;
    public Type elementType;
    
    private Type(boolean isScalar, Type elementType) {
        this.isScalar = isScalar;
        this.elementType = elementType;
    }

    boolean isScalar() {
        return isScalar;
    }
    
    boolean isVector() {
        return elementType != null;
    }
    
    Type getArrayElementType() {
        return elementType;
    }
    
    boolean isVoid() {
        return this == VOID;
    }
}
