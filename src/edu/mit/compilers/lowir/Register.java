package edu.mit.compilers.lowir;

/**
 * Manages the value stored in registers and de/allocates register space
 * for instructions
 */
public class Register extends Storage {
    private Object value;

    public Register(String name) {
        super(name);
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return this.value;
    }

    public static Register create(String name) {
        return new Register(name);
    }
}