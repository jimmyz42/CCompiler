package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import edu.mit.compilers.cfg.CFGContext;
import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.grammar.DecafParser.MethodCallStmtContext;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.highir.descriptor.Descriptor;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.optimizer.Optimizable;
import edu.mit.compilers.optimizer.OptimizerContext;

public class MethodCallStmt extends Statement implements Optimizable {
    private MethodCallExpr methodCall;
    public MethodCallStmt(MethodCallExpr methodCall) {
        this.methodCall = methodCall;
    }

    public static MethodCallStmt create(DecafSemanticChecker checker, MethodCallStmtContext ctx) {
        return new MethodCallStmt(MethodCallExpr.create(checker, ctx.method_call()));
    }

    @Override
    public void prettyPrint(PrintWriter pw, String prefix) {
        super.prettyPrint(pw, prefix);
        methodCall.prettyPrint(pw, prefix+"    ");
    }

    @Override
    public void cfgPrint(PrintWriter pw, String prefix) {
        methodCall.cfgPrint(pw, prefix);
        pw.println();
    }

    @Override
    public CFG generateCFG(CFGContext context) {
    	return BasicBlock.create(this);
    }

    @Override
    public void generateAssembly(AssemblyContext ctx){
        methodCall.generateAssembly(ctx);
    }

	@Override
	public long getNumStackAllocations() {
		return methodCall.getNumStackAllocations();
	}

	@Override
	public Set<Descriptor> getConsumedDescriptors() {
		return methodCall.getConsumedDescriptors();
	}

	@Override
	public Set<Descriptor> getGeneratedDescriptors() {
		return Collections.emptySet();
	}
	
	@Override
	public Optimizable doConstantFolding() {
		this.methodCall = (MethodCallExpr)methodCall.doConstantFolding();
		return this;
	}
	
	@Override
	public Optimizable algebraSimplify() {
		this.methodCall = (MethodCallExpr)methodCall.algebraSimplify();
		return this;
	}

	@Override
	public boolean isLinearizable() {
		return false;
	}

	@Override
	public List<Optimizable> generateTemporaries(OptimizerContext context, boolean skipGeneration) {
		return Collections.singletonList(this);
	}

	@Override
	public void doCSE(OptimizerContext ctx) {
		methodCall.doCSE(ctx);
	}

    	@Override
   	public void doCopyPropagation(OptimizerContext ctx) {
        	methodCall.doCopyPropagation(ctx); //TODO: maybe not needed
    }

    @Override
    public void doConstantPropagation(OptimizerContext ctx){
        methodCall.doConstantPropagation(ctx);
    }   
}
