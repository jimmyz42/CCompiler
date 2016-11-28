package edu.mit.compilers.optimizer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import edu.mit.compilers.highir.descriptor.VariableDescriptor;
import edu.mit.compilers.highir.nodes.Expression;
import edu.mit.compilers.highir.nodes.IdLocation;
import edu.mit.compilers.highir.nodes.Literal;
import edu.mit.compilers.highir.nodes.Location;

public class OptimizerContext {
	private int tempVarNonce;

	private HashMap<Location, Integer> varToVal = new HashMap<>();
	private HashMap<Expression, Integer> exprToVal = new HashMap<>();
	private HashMap<Expression, Location> exprToTemp = new HashMap<>();
	
	// Set of expressions containing a given variable
	private HashMap<Location, Set<Expression>> exprsContainingVar = new HashMap<>();

	private HashMap<Expression, Location> cseExprToVar = new HashMap<>();
	private HashMap<Location, Set<Expression>> cseVarToExprs = new HashMap<>();

	//Copy Propagation Maps
	private HashMap<Location, Location> cpTempToVar = new HashMap<>();
	private HashMap<Location, Set<Location>> cpVarToSet = new HashMap<>();

	//Constant Propagation Maps
	private HashMap<Location, Literal> varToConst = new HashMap<>();

	public HashMap<Location, Literal> getVarToConst(){
		return varToConst;
	}

	public HashMap<Location, Location> getCPTempToVar() {
		return cpTempToVar;
	}

	public HashMap<Location, Set<Location>> getCPVarToSet() {
		return cpVarToSet;
	}

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
			desc.setToTemp();
			IdLocation loc = IdLocation.create(desc);
			varToVal.put(loc, tempVarNonce);
			exprToVal.put(expr, tempVarNonce);
			exprToTemp.put(expr, loc);
			tempVarNonce++;
			
			for(Location var: expr.getLocationsUsed()) {
				if(!exprsContainingVar.containsKey(var)) {
					exprsContainingVar.put(var, new HashSet<>());
				}
				exprsContainingVar.get(var).add(expr);
			}
			return true;
		}
	}
}
