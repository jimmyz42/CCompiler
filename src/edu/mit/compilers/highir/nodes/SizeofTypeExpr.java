package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import edu.mit.compilers.cfg.CFGAble;
import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.highir.descriptor.Descriptor;
import edu.mit.compilers.highir.descriptor.VariableDescriptor;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.ImmediateValue;
import edu.mit.compilers.optimizer.OptimizerContext;

public class SizeofTypeExpr extends Expression {
    private Type type;

    private SizeofTypeExpr(Type type) {
        this.type = type;
    }

    public static SizeofTypeExpr create(DecafSemanticChecker checker, DecafParser.SizeofTypeExprContext ctx) {
        return new SizeofTypeExpr(ScalarType.create(checker, ctx.type()));
    }

    // This is the return type, which is NOT the same as this.type:
    // e.g. `sizeof(bool)` is an int
    @Override
    public Type getType() {
        return ScalarType.INT;
    }

    @Override
    public void cfgPrint(PrintWriter pw, String prefix) {
    	pw.print(prefix + "sizeof(" + type + ")");
    }
    
    @Override
    public void generateAssembly(AssemblyContext ctx) {
		ctx.storeStack(getStorageTuple(), ImmediateValue.create(this.type.getSize()));
    }

	@Override
	public long getNumStackAllocations() {
		return 1;
	}
	
    @Override
    public ImmediateValue getLocation(AssemblyContext ctx) {
        return ImmediateValue.create(this.type.getSize());
    }

	@Override
	public Set<Descriptor> getConsumedDescriptors() {
		return Collections.emptySet();
	}

	@Override
	public Set<Descriptor> getGeneratedDescriptors() {
		return Collections.emptySet();
	}

	@Override
	public List<CFGAble> generateTemporaries(OptimizerContext context) {
		return Collections.emptyList();
	}
	
	@Override
    public int hashCode() {
        return ImmediateValue.create(this.type.getSize()).hashCode();
    }
}