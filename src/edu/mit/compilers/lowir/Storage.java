package edu.mit.compilers.lowir;

/**
 * Manages the value stored in registers and de/allocates register space
 * for instructions
 */
abstract public class Storage {
    private String name;

    public Storage(String name) {
        this.name = name;
    }
    
    public Storage() {
    	
    }

    public String getName() {
        return this.name;
    }
    
    @Override
    public String toString(){
    	return this.name; 
    }
}