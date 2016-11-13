package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

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
import edu.mit.compilers.lowir.instructions.Not;
import edu.mit.compilers.lowir.instructions.Or;
import exceptions.TypeMismatchError;

public class NotExpr extends Expression {
    private Expression expression;

    public NotExpr(Expression expression) {
        this.expression = expression;
    }

    public static NotExpr create(DecafSemanticChecker checker, DecafParser.NotExprContext ctx) {
        Expression expression = (Expression) checker.visit(ctx.expr());
        if (expression.getType() != ScalarType.BOOL) {
            throw new TypeMismatchError("Expected a bool expression", ctx.expr());
        }

        return new NotExpr(expression);
    }

    @Override
    public Type getType() {
        // TODO Auto-generated method stub
        return ScalarType.BOOL;
    }

    @Override
    public BasicBlock shortCircuit(CFG trueBranch, CFG falseBranch) {
    	return expression.shortCircuit(falseBranch, trueBranch);
    }

    @Override
    public void cfgPrint(PrintWriter pw, String prefix) {
    	pw.print(prefix + "!");
    	expression.cfgPrint(pw, "");
    }

    @Override
    public CFG generateCFG(CFGContext context) {
    	CFG expressionCFG =  expression.generateCFG(context);
    	VariableDescriptor temp = context.generateNewTemporary(getType());
    	List<CFGAble> components = new ArrayList<>();
    	components.add(temp);
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
        ctx.addInstruction(Not.create(dest));

		ctx.storeStack(getStorageTuple(), dest);
        ctx.deallocateRegister(src);
        ctx.deallocateRegister(dest);
    }

	@Override
	public long getNumStackAllocations() {
		return expression.getNumStackAllocations() + 1;
	}
}