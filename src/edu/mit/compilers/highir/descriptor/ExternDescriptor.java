package edu.mit.compilers.highir.descriptor;

import java.io.PrintWriter;

import edu.mit.compilers.cfg.CFGContext;
import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.highir.nodes.ScalarType;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.ImmediateValue;
import edu.mit.compilers.lowir.instructions.Extern;

public class ExternDescriptor extends FunctionDescriptor {
    public ExternDescriptor(String name) {
        super(name, ScalarType.INT);
    }
    
    @Override
    public String getExpressionName() {
        return getName() + "@PLT";
    }

    @Override
    public void prettyPrint(PrintWriter pw, String prefix) {
        pw.println(prefix + "extern " + getName());
    }

    @Override
    public void generateAssembly(AssemblyContext ctx) {
    }

	@Override
	public long getNumStackAllocations() {
		return 0;
	}
}
