package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;

import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.ImmediateValue;

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
    public Type getExpressionType() {
        return ScalarType.INT;
    }

    @Override
    public void cfgPrint(PrintWriter pw, String prefix) {
    	pw.print(prefix + "sizeof(" + type + ")");
    }
    
    @Override
    public void generateAssembly(AssemblyContext ctx) {
    	System.out.println("size of " + this.type.toString() + " is " + this.type.getSize());
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
}