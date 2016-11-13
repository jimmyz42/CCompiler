package edu.mit.compilers.cfg;

import java.io.PrintWriter;
import java.util.Set;

import edu.mit.compilers.highir.descriptor.Descriptor;
import edu.mit.compilers.lowir.AssemblyContext;

public interface Condition {
	// Represents a boolean condition for jumping
	// TODO generate corresponding jump statements
	// BoolLiteral also implements this TODO generate unconditional jump
	
	public void generateAssembly(AssemblyContext ctx);
	public void cfgPrint(PrintWriter pw, String prefix);
    public long getNumStackAllocations();
    public Set<Descriptor> getConsumedDescriptors();
}
