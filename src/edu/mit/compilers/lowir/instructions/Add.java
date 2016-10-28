package edu.mit.compilers.lowir.instructions;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.Location;

public class Add extends Instruction {
	Location src;
	Location dest;
	
    public Add(Location src, Location dest) {
    	this.src = src; 
    	this.dest = dest;
    }

    @Override
    public void printAssembly(PrintWriter pw, String prefix) {
        pw.print(prefix + "add " + src + ", " + dest);
    }
}