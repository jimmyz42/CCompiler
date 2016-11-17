package edu.mit.compilers.optimizer;

import java.util.HashMap;
import java.util.Set;

import edu.mit.compilers.highir.descriptor.Descriptor;
import edu.mit.compilers.highir.descriptor.VariableDescriptor;
import edu.mit.compilers.highir.nodes.Expression;
import edu.mit.compilers.highir.nodes.IdLocation;
import edu.mit.compilers.highir.nodes.Location;

public class OptimizerContext {
	private int tempVarNonce;

	private HashMap<Location, Integer> varToVal = new HashMap<>();
	private HashMap<Expression, Integer> exprToVal = new HashMap<>();
	private HashMap<Expression, Location> exprToTemp = new HashMap<>();

	private HashMap<Expression, Location> cseExprToVar = new HashMap<>();
	private HashMap<Location, Set<Expression>> cseVarToExprs = new HashMap<>();

	public HashMap<Location, Integer> getVarToVal() {
		return varToVal;
	}

	public HashMap<Expression, Integer> getExprToVal() {
		return exprToVal;
	}

	public HashMap<Expression, Location> getExprToTemp() {
		return exprToTemp;
	}
	
	public HashMap<Expression, Location> getCSEExprToVar() {
		return cseExprToVar;
	}
	
	public HashMap<Location, Set<Expression>> getCSEVarToExprs() {
		return cseVarToExprs;
	}
	
	public void addVariable(Location loc, Expression expr) {
		varToVal.put(loc, exprToVal.get(expr));
	}
	
	/**
	 * 
	 * @param expr
	 * @return true if a new variable was created
	 */
	public boolean addExpression(Expression expr) {
		if(exprToVal.containsKey(expr)) {
			return false;
		} else {
			VariableDescriptor desc = VariableDescriptor.create("t"+tempVarNonce, expr.getType(), false);;
			IdLocation loc = IdLocation.create(desc);
			varToVal.put(loc, tempVarNonce);
			exprToVal.put(expr, tempVarNonce);
			exprToTemp.put(expr, loc);
			tempVarNonce++;
			return true;
		}
	}
}
