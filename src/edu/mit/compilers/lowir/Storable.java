package edu.mit.compilers.lowir;

import edu.mit.compilers.lowir.AssemblyContext;

public interface Storable {
    public Storage getLocation(AssemblyContext ctx);
    public Storage getLocation(AssemblyContext ctx, boolean forceStackLocation);
    public Register allocateRegister(AssemblyContext ctx);
    public long getNumStackAllocations();
}