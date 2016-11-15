package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.mit.compilers.cfg.CFGAble;
import edu.mit.compilers.cfg.CFGContext;
import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.highir.descriptor.Descriptor;
import edu.mit.compilers.highir.descriptor.VariableDescriptor;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.Register;
import edu.mit.compilers.lowir.instructions.Mov;
import edu.mit.compilers.optimizer.OptimizerContext;
import exceptions.TypeMismatchError;

public class AssignStmt extends Statement {
	private Location location;
	private Expression expression;
	private String assignOp;

	public AssignStmt(Location location, String assignOp, Expression expression) {
		this.location = location;
		this.assignOp = assignOp;
		this.expression = expression;
	}

	public static AssignStmt create(DecafSemanticChecker checker, DecafParser.AssignStmtContext ctx) {
		Location location = Location.create(checker, ctx.location());
		Expression expression = Expression.create(checker, ctx.expr());
		String assignOp = ctx.assign_op().getText();
		return create(location, assignOp, expression, ctx);
	}

	public static AssignStmt create(Location location, String assignOp, Expression expression, DecafParser.StatementContext ctx) {
		if (location.getType() != expression.getType()) {
			throw new TypeMismatchError("Two sides of an assignment should have the same type", ctx);
		}
		if (!(location.getType() instanceof ScalarType)) {
			throw new TypeMismatchError("Can only assign a scalar", ctx);
		}
		if (!assignOp.equals("=")) {
			if (location.getType() != ScalarType.INT) {
				throw new TypeMismatchError("Can only use += and -= on ints", ctx);
			}
			if (assignOp.equals("+=")) {
				expression = new AddOpExpr(new AddOp("+"), location, expression);
			} else if (assignOp.equals("-=")) {
				expression = new AddOpExpr(new AddOp("-"), location, expression);
			}
		}

		return new AssignStmt(location, "=", expression);
	}

	public static AssignStmt create(Location location, String assignOp, Expression expression) {
		return new AssignStmt(location, "=", expression);
	}

	@Override
	public void prettyPrint(PrintWriter pw, String prefix) {
		super.prettyPrint(pw, prefix);
		pw.println(prefix + "-location:");
		location.prettyPrint(pw, prefix + "    ");
		pw.println(prefix+ "-operator: " + assignOp);
		pw.println(prefix + "-expression:");
		expression.prettyPrint(pw, prefix + "    ");
	}

	@Override
	public void cfgPrint(PrintWriter pw, String prefix) {
		pw.print(prefix);
		location.cfgPrint(pw,"");
		pw.print(" " + assignOp);
		expression.cfgPrint(pw," ");
		pw.println();
	}

	@Override
	public CFG generateCFG(CFGContext context) {
		return BasicBlock.create(this);
	}

	@Override
	public void generateAssembly(AssemblyContext ctx){
		expression.generateAssembly(ctx);
		location.generateAssembly(ctx);

		Register src = expression.allocateRegister(ctx);
		ctx.addInstruction(Mov.create(src, location.getLocation(ctx)));
		ctx.deallocateRegister(src);
	}

	@Override
	public long getNumStackAllocations() {
		return expression.getNumStackAllocations() + location.getNumStackAllocations();
	}

	@Override
	public Set<Descriptor> getConsumedDescriptors() {
		return expression.getConsumedDescriptors();
	}

	@Override
	public Set<Descriptor> getGeneratedDescriptors() {
		return location.getGeneratedDescriptors();
	}

	@Override
	public List<CFGAble> generateTemporaries(OptimizerContext context) {
		context.addVariable(location.getVariable(), expression);
		
		List<CFGAble> temps = new ArrayList<>();
		temps.addAll(expression.generateTemporaries(context));

    	VariableDescriptor temp = context.addExpression(expression);
		temps.add(temp);
		temps.add(this);
		temps.add(AssignStmt.create(IdLocation.create(temp), "=", location));

		return temps;
	}
}