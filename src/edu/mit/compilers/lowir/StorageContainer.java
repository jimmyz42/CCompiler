package edu.mit.compilers.lowir;

/**
 * Manages the value stored in registers and de/allocates register space
 * for instructions
 */
abstract public class StorageContainer extends Storage {

    public StorageContainer(String name) {
    	super(name);
    }
}