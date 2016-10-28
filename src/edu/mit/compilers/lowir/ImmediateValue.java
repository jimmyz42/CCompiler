package edu.mit.compilers.lowir;

/**
 * Manages the value stored in registers and de/allocates register space
 * for instructions
 */
public class ImmediateValue extends Location {
    public ImmediateValue(String value) {
    	super(value);
    }
    
    public static ImmediateValue create(Long value) {
    	return new ImmediateValue(value.toString());
    }
    
    public static ImmediateValue create(long value) {
    	return new ImmediateValue(new Long(value).toString());
    }
    
    public static ImmediateValue create(Boolean value) {
    	return new ImmediateValue(value.toString());
    }

    public static ImmediateValue create(String value) {
    	return new ImmediateValue(value);
    }
}