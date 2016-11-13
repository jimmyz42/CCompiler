package edu.mit.compilers.highir.nodes;

import edu.mit.compilers.cfg.CFGAble;
import edu.mit.compilers.cfg.CFGContext;
import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.highir.descriptor.VariableDescriptor;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.Register;
import edu.mit.compilers.lowir.Storage;
import edu.mit.compilers.lowir.instructions.Mov;
import edu.mit.compilers.lowir.instructions.Neg;
import edu.mit.compilers.lowir.instructions.Not;
import edu.mit.compilers.lowir.instructions.Or;
import exceptions.TypeMismatchError;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class NegExpr extends Expression {
    private Expression expression;

    public NegExpr(Expression expression) {
        this.expression = expression;
    }

    public static NegExpr create(DecafSemanticChecker checker, DecafParser.NegExprContext ctx) {
        Expression expression = (Expression) checker.visit(ctx.expr());
        if (expression.getType() != ScalarType.INT) {
            throw new TypeMismatchError("Expected an int expression", ctx.expr());
        }

        return new NegExpr(expression);
    }

    @Override
    public Type getType() {
        return ScalarType.INT;
    }

    @Override
    public void prettyPrint(PrintWriter pw, String prefix){
    	super.prettyPrint(pw, prefix);
    	pw.print(prefix + "-expression:");
    	expression.prettyPrint(pw, prefix + "    ");
    }

    @Override
    public void cfgPrint(PrintWriter pw, String prefix) {
    	pw.print(prefix + "-");
    	expression.cfgPrint(pw, "");
    }

    @Override
    public CFG generateCFG(CFGContext context) {
    	CFG expressionCFG =  expression.generateCFG(context);
    	VariableDescriptor temp = context.generateNewTemporary(getType());
    	List<CFGAble> components = new ArrayList<>();
    	components.add(BasicBlock.create(temp));
    	components.add(AssignStmt.create(IdLocation.create(temp), "=", this));
    	BasicBlock thisCFG = BasicBlock.create(components);
    	
    	expressionCFG.addNextBlock(thisCFG.getEntryBlock());
    	thisCFG.addPreviousBlock(expressionCFG.getExitBlock());
    	
        return new CFG(expressionCFG.getEntryBlock(), thisCFG.getExitBlock());
    }

    @Override
    public void generateAssembly(AssemblyContext ctx){
        expression.generateAssembly(ctx);

        Register src = expression.allocateRegister(ctx);
        Register dest = ctx.allocateRegister(getStorageTuple());
        ctx.addInstruction(Mov.create(src, dest));
        ctx.addInstruction(Neg.create(dest));

		ctx.storeStack(getStorageTuple(), dest);
        ctx.deallocateRegister(src);
        ctx.deallocateRegister(dest);
    }

	@Override
	public long getNumStackAllocations() {
		return expression.getNumStackAllocations() + 1;
	}
}