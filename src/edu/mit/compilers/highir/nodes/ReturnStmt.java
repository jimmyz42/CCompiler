package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import edu.mit.compilers.cfg.CFGContext;
import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.highir.descriptor.Descriptor;
import edu.mit.compilers.highir.descriptor.VariableDescriptor;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.Register;
import edu.mit.compilers.lowir.Storage;
import edu.mit.compilers.lowir.instructions.Mov;
import edu.mit.compilers.optimizer.Optimizable;
import edu.mit.compilers.optimizer.OptimizerContext;
import exceptions.TypeMismatchError;

public class ReturnStmt extends Statement implements Optimizable {
	private Expression expression;

	public ReturnStmt(Expression expression) {
		this.expression = expression;
	}

	public static ReturnStmt create(DecafSemanticChecker checker, DecafParser.ReturnStmtContext ctx) {
		Type methodType = checker.currentMethodDescriptor().getType();
		if(ctx.expr() == null) {
			if(!(methodType instanceof VoidType)) {
				throw new TypeMismatchError("Empty return value are only allowed in void methods", ctx);
			}
			return new ReturnStmt(null);
		}

		Expression expression = Expression.create(checker, ctx.expr());
		if(expression.getType() != methodType) {
			throw new TypeMismatchError("Return type " + expression.getType() + " does not match the expected type " + methodType, ctx);
		}
		return new ReturnStmt(expression);
	}

	public static ReturnStmt create() {
		return new ReturnStmt(null);
	}

	@Override
	public void prettyPrint(PrintWriter pw, String prefix) {
		super.prettyPrint(pw, prefix);
		pw.println(prefix + "-return:");
		if(expression != null) {
			expression.prettyPrint(pw, prefix + "    ");
		}
	}

	@Override
	public void cfgPrint(PrintWriter pw, String prefix) {
		pw.print(prefix + "return ");
		if(expression != null) {
			expression.cfgPrint(pw, "");
		}
		pw.println();
	}

	@Override
	public CFG generateCFG(CFGContext context) {
		BasicBlock block = BasicBlock.create(this);
		BasicBlock methodEnd = context.currentMethodCFG().getExitBlock();
		block.setNextBlock(methodEnd);
		methodEnd.addPreviousBlock(block);

		if(expression != null) {
			CFG expressionCFG = expression.generateCFG(context);
			expressionCFG.setNextBlock(block);
			block.setPreviousBlock(expressionCFG.getExitBlock());
			return new CFG(expressionCFG.getEntryBlock(), block);
		}
		return block;
	}

	@Override
	public void generateAssembly(AssemblyContext ctx) {
		if(expression != null) {
			expression.generateAssembly(ctx);
			Storage returnValue = expression.getLocation(ctx);
			ctx.addInstruction(Mov.create(returnValue, Register.create("%rax")));
		}
	}

	@Override
	public long getNumStackAllocations() {
		if(expression == null) return 0;
		return expression.getNumStackAllocations();
	}

	@Override
	public Set<Descriptor> getConsumedDescriptors() {
		if(expression != null) {
			return expression.getConsumedDescriptors();
		}
		return Collections.emptySet();
	}

	@Override
	public Set<Descriptor> getGeneratedDescriptors() {
		return Collections.emptySet();
	}

	@Override
	public List<Optimizable> generateTemporaries(OptimizerContext context) {
		return Collections.singletonList((Optimizable)this);
	}

	@Override
	public void doCSE(OptimizerContext ctx) {
		if(expression != null) {
			VariableDescriptor temp = ctx.getCSEExprToVar().get(expression);

			if(temp != null) {
				expression = new IdLocation(temp);
				ctx.getCSEExprToVar().put(expression, temp);

			} else {
				expression.doCSE(ctx);
			}
		}
	}
}
