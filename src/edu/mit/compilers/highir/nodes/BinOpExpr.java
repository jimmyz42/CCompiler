package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.mit.compilers.highir.descriptor.Descriptor;
import edu.mit.compilers.highir.descriptor.VariableDescriptor;
import edu.mit.compilers.optimizer.Optimizable;
import edu.mit.compilers.optimizer.OptimizerContext;


abstract public class BinOpExpr extends Expression {
	protected BinOp operator;
	protected Expression lhs;
	protected Expression rhs;

	public BinOpExpr(BinOp operator, Expression lhs, Expression rhs) {
		this.operator = operator;
		this.lhs = lhs;
		this.rhs = rhs;
	}

	@Override
	public void prettyPrint(PrintWriter pw, String prefix) {
		pw.println(prefix + getClass().getSimpleName());
		pw.println(prefix + "-lhs:");
		lhs.prettyPrint(pw, prefix + "    ");
		pw.println(prefix + "-operator: " + operator);
		pw.println(prefix + "-rhs:");
		rhs.prettyPrint(pw, prefix + "    ");
	}

	@Override
	public void cfgPrint(PrintWriter pw, String prefix) {
		pw.print(prefix);
		lhs.cfgPrint(pw, "");
		pw.print(" " + operator.getTerminal());;
		rhs.cfgPrint(pw, " ");
	}

	@Override
	public Set<Descriptor> getConsumedDescriptors() {
		Set<Descriptor> consumed = new HashSet<>();
		consumed.addAll(lhs.getConsumedDescriptors());
		consumed.addAll(rhs.getConsumedDescriptors());
		return consumed;
	}

	@Override
	public Set<Descriptor> getGeneratedDescriptors() {
		return Collections.emptySet();
	}

	@Override
	public List<Optimizable> generateTemporaries(OptimizerContext context) {
		List<Optimizable> temps = new ArrayList<>();

		temps.addAll(lhs.generateTemporaries(context));
		temps.addAll(rhs.generateTemporaries(context));

		if(context.addExpression(this)) {
			VariableDescriptor temp = context.getExprToTemp().get(this);
			temps.add(temp);
			temps.add(AssignStmt.create(IdLocation.create(temp), "=", this));
		}

		return temps;
	}

	@Override
	public void doCSE(OptimizerContext ctx) {
		VariableDescriptor lhsTemp = ctx.getCSEExprToVar().get(lhs);
		VariableDescriptor rhsTemp = ctx.getCSEExprToVar().get(rhs);

		if(lhsTemp != null) {
			lhs = new IdLocation(lhsTemp);
			ctx.getCSEExprToVar().put(lhs, lhsTemp);

		} else {
			lhs.doCSE(ctx);
		}
		if(rhsTemp != null) {
			rhs = new IdLocation(rhsTemp);
			ctx.getCSEExprToVar().put(rhs, rhsTemp);

		} else {
			rhs.doCSE(ctx);
		}
	}

	@Override
	public int hashCode() {
		return ("" + lhs.hashCode() + operator.hashCode() + rhs.hashCode()).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return hashCode() == obj.hashCode();
	}
}
