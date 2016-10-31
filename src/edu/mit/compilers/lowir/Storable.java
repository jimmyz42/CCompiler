package edu.mit.compilers.lowir;

import edu.mit.compilers.lowir.AssemblyContext;

public interface Storable {
    public Storage getLocation(AssemblyContext ctx);
    public Register allocateRegister(AssemblyContext ctx);
    public void deallocateRegister(AssemblyContext ctx);
}
