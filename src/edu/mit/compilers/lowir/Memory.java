package edu.mit.compilers.lowir;

/**
 * Manages the value stored in registers and de/allocates register space
 * for instructions
 */
public class Memory extends Location {

    public Memory(String name) {
    	super(name);
    }
    
    public static Memory create(String name) {
    	return new Memory(name);
    }
}