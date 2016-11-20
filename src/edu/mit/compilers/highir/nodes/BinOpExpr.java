package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.mit.compilers.highir.descriptor.Descriptor;
import edu.mit.compilers.highir.nodes.Location;
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
		System.out.println("generateTemporaries");
		List<Optimizable> temps = new ArrayList<>();

		temps.addAll(lhs.generateTemporaries(context));
		temps.addAll(rhs.generateTemporaries(context));

		if(context.addExpression(lhs)) {
			Location lhsTemp = context.getExprToTemp().get(lhs);
			temps.add(lhsTemp.getVariable());
		}
		if(context.addExpression(rhs)) {
			Location rhsTemp = context.getExprToTemp().get(rhs);
			temps.add(rhsTemp.getVariable());
		}
		Location lhsTemp = context.getExprToTemp().get(lhs);
		Location rhsTemp = context.getExprToTemp().get(rhs);

		if(lhsTemp == rhsTemp) {
			temps.add(AssignStmt.create(lhsTemp, "=", lhs));
		} else {
			temps.add(AssignStmt.create(lhsTemp, "=", lhs));
			temps.add(AssignStmt.create(rhsTemp, "=", rhs));
		}
		return temps;
	}

	@Override
	public void doConstantPropagation(OptimizerContext ctx){
		if(lhs instanceof Location){
			Location lhsLoc = (Location)lhs;
			//is it in the map? 
			if(ctx.getVarToConst().containsKey(lhsLoc)){
				lhs = ctx.getVarToConst().get(lhsLoc); //replace var with const
			}
		}
		if(rhs instanceof Location){
			Location rhsLoc = (Location)rhs;
			//is it in the map? 
			if(ctx.getVarToConst().containsKey(rhsLoc)){
				rhs = ctx.getVarToConst().get(rhsLoc); //replace var with const
			}
		}
	}

	@Override
	public void doCopyPropagation(OptimizerContext ctx){
		if(lhs instanceof Location){
			Location lhsLoc = (Location)lhs;
			if(lhsLoc.getVariable().isTemp()){
				if(ctx.getCPTempToVar().containsKey(lhsLoc)){
					Location var = ctx.getCPTempToVar().get(lhsLoc);
					lhs = var; //put var there instead of temp
				}
			}
		}
		if(rhs instanceof Location){
			Location rhsLoc = (Location)rhs;
			if(rhsLoc.getVariable().isTemp()){
				if(ctx.getCPTempToVar().containsKey(rhsLoc)){
					Location var = ctx.getCPTempToVar().get(rhsLoc);
					rhs = var; //put var there instead of temp
				}
			}
		}
	}

	@Override
	public void doCSE(OptimizerContext ctx) {
		System.out.println("doCSE");
		Location lhsTemp = ctx.getCSEExprToVar().get(lhs);
		Location rhsTemp = ctx.getCSEExprToVar().get(rhs);
		System.out.println(lhs);
		System.out.println(lhs.hashCode());
		System.out.println(rhs);
		System.out.println(rhs.hashCode());
		
		if(lhsTemp != null) {
			lhs = lhsTemp;
			ctx.getCSEExprToVar().put(lhs, lhsTemp);

		} else {
			lhs.doCSE(ctx);
		}
		if(rhsTemp != null) {
			rhs = rhsTemp;
			ctx.getCSEExprToVar().put(rhs, rhsTemp);

		} else {
			rhs.doCSE(ctx);
		}
	}

	@Override
	public int hashCode() {
		return ("binop" + lhs.hashCode() + operator.hashCode() + rhs.hashCode()).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return hashCode() == obj.hashCode();
	}
}
