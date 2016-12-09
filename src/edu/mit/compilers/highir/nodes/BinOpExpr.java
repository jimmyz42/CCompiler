package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.BitSet;

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
	public boolean isLinearizable() {
		return lhs.isLinearizable() && rhs.isLinearizable();
	}
	
	@Override
	public Set<Location> getLocationsUsed() {
		Set<Location> locs = new HashSet<>();
		locs.addAll(lhs.getLocationsUsed());
		locs.addAll(rhs.getLocationsUsed());
		return locs;		
	}

	@Override
	public List<Optimizable> generateTemporaries(OptimizerContext context, boolean skipGeneration) {
		List<Optimizable> temps = new ArrayList<>();

		temps.addAll(lhs.generateTemporaries(context, false));
		temps.addAll(rhs.generateTemporaries(context, false));

		if(!skipGeneration && isLinearizable()) {
			if(context.addExpression(this)) {
				Location temp = context.getExprToTemp().get(this);
				if(!context.getCSEDeclaredTemps().contains(temp)) {
					temps.add(temp.getVariable());
					context.getCSEDeclaredTemps().add(temp);
				}
				temps.add(AssignStmt.create(temp, "=", this.clone()));
			}
		}
		return temps;
	}

	@Override
	public void doConstantPropagation(OptimizerContext ctx) {
		if(lhs instanceof Location){
			Location lhsLoc = (Location)lhs;
			//is it in the map?
			if(ctx.getVarToConst().containsKey(lhsLoc)){
				lhs = ctx.getVarToConst().get(lhsLoc); //replace var with const
			}
		} else
			lhs.doConstantPropagation(ctx);
		
		if(rhs instanceof Location){
			Location rhsLoc = (Location)rhs;
			//is it in the map?
			if(ctx.getVarToConst().containsKey(rhsLoc)){
				rhs = ctx.getVarToConst().get(rhsLoc); //replace var with const
			}
		} else
			rhs.doConstantPropagation(ctx);
	}

	@Override
	public void doGlobalConstantPropagation(OptimizerContext ctx) {
		if(lhs instanceof Location){
			Location lhsLoc = (Location)lhs;
			VariableDescriptor var = lhsLoc.getVariable();
			List<Long> consts = new ArrayList<>();
			boolean allConst = true;
			//TODO: also check w/ gen in block
			//check all reaching definitions
			if(ctx.getVarToDefs().containsKey(var)){
				for(Integer def : ctx.getVarToDefs().get(var)){
					//is this definition alive?
					if(ctx.getRdIn().containsKey(ctx.getCurrentBlock())){
						if(ctx.getRdIn().get(ctx.getCurrentBlock()).get(def)){
							//does it assign var to const?
							AssignStmt stmt = ctx.getIntToAssignStmt().get(def);
							if(stmt.assignsToConstant()){
								consts.add(stmt.whatConst());
							} else {
								allConst = false;
							}
						}						
					}
				}
			}

			//if all assign var to same const
			//replace with constant
			if(allConst){
				if(consts.size() == 1){
					lhs = new IntLiteral(consts.get(0));
				}
			}
		} else
			lhs.doConstantPropagation(ctx);
		
		if(rhs instanceof Location){
			Location rhsLoc = (Location)rhs;
			VariableDescriptor var = rhsLoc.getVariable();
			List<Long> consts = new ArrayList<>();
			boolean allConst = true;

			//check all reaching definitions
			if(ctx.getVarToDefs().containsKey(var)){
				for(Integer def : ctx.getVarToDefs().get(var)){
					//is this definition alive? 
					if(ctx.getRdIn().containsKey(ctx.getCurrentBlock())){
						if(ctx.getRdIn().get(ctx.getCurrentBlock()).get(def)){
							//does it assign var to const?
							AssignStmt stmt = ctx.getIntToAssignStmt().get(def);
							if(stmt.assignsToConstant()){
								consts.add(stmt.whatConst());
							}else{
								allConst = false;
							}
						}						
					}
				}
			}


			//if all assign var to same const
			//replace with constant
			if(allConst){
				if(consts.size() == 1){
					rhs = new IntLiteral(consts.get(0));
				}
			}
		} else
			rhs.doConstantPropagation(ctx);
	}

	@Override
	public void makeUseSet(OptimizerContext ctx, BitSet use){
		lhs.makeUseSet(ctx, use);
		rhs.makeUseSet(ctx, use);
	}

	@Override
	public void doCopyPropagation(OptimizerContext ctx) {
		if(lhs instanceof Location){
			Location lhsLoc = (Location)lhs;
			if(ctx.getCPTempToVar().containsKey(lhsLoc)){
				Location var = ctx.getCPTempToVar().get(lhsLoc);
				lhs = var; //put var there instead of temp
			}
		} else
			lhs.doCopyPropagation(ctx);
		
		if(rhs instanceof Location){
			Location rhsLoc = (Location)rhs;
			if(ctx.getCPTempToVar().containsKey(rhsLoc)){
				Location var = ctx.getCPTempToVar().get(rhsLoc);
				rhs = var; //put var there instead of temp
			}
		} else
			rhs.doCopyPropagation(ctx);
	}

	@Override
	public void doCSE(OptimizerContext ctx) {
		Expression origLHS = lhs.clone();
		if(ctx.getCSEAvailableExprs().contains(lhs) 
				&& ctx.getExprToTemp().get(lhs) != null) {
			lhs = ctx.getExprToTemp().get(lhs);
		} else {
			lhs.doCSE(ctx);
		}
		ctx.getCSEAvailableExprs().add(origLHS);
		
		Expression origRHS = rhs.clone();
		if(ctx.getCSEAvailableExprs().contains(rhs) 
				&& ctx.getExprToTemp().get(rhs) != null) {
			rhs = ctx.getExprToTemp().get(rhs);
		} else {
			rhs.doCSE(ctx);
		}
		ctx.getCSEAvailableExprs().add(origRHS);
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
