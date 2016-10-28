package edu.mit.compilers.lowir;

/**
 * Manages the value stored in registers and de/allocates register space
 * for instructions
 */
abstract public class Location {
    private String name;

    public Location(String name) {
        this.name = name;
    }
    
    public Location() {
    	
    }

    public String getName() {
        return this.name;
    }
    
    @Override
    public String toString(){
    	return this.name; 
    }
}