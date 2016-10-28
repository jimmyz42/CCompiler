package edu.mit.compilers.lowir;

import java.io.PrintWriter;

/**
 * Manages the value stored in registers and de/allocates register space
 * for instructions
 */
abstract public class Storage {
    private String name;
    private Object value;

    public Storage(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public Storage(String name) {
        this.name = name;
        this.value = 0;
    }

    public String getName() {
        return this.name;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return this.value;
    }
    
    @Override
    public String toString(){
    	return this.name; 
    }
    
    public void printAssembly(PrintWriter pw, String prefix){
    	pw.print(prefix + this.name); 
    }
}