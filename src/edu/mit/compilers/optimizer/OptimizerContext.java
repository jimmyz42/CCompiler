package edu.mit.compilers.optimizer;

import java.util.HashMap;
import java.util.Set;

import edu.mit.compilers.highir.descriptor.Descriptor;
import edu.mit.compilers.highir.descriptor.VariableDescriptor;
import edu.mit.compilers.highir.nodes.Expression;

public class OptimizerContext {
	private int tempVarNonce;

	private HashMap<Descriptor, Integer> varToVal = new HashMap<>();
	private HashMap<Expression, Integer> exprToVal = new HashMap<>();
	private HashMap<Expression, VariableDescriptor> exprToTemp = new HashMap<>();

	private HashMap<Expression, VariableDescriptor> cseExprToVar = new HashMap<>();
	private HashMap<Descriptor, Set<Expression>> cseVarToExprs = new HashMap<>();

	public HashMap<Descriptor, Integer> getVarToVal() {
		return varToVal;
	}

	public HashMap<Expression, Integer> getExprToVal() {
		return exprToVal;
	}

	public HashMap<Expression, VariableDescriptor> getExprToTemp() {
		return exprToTemp;
	}
	
	public HashMap<Expression, VariableDescriptor> getCSEExprToVar() {
		return cseExprToVar;
	}
	
	public HashMap<Descriptor, Set<Expression>> getCSEVarToExprs() {
		return cseVarToExprs;
	}
	
	public void addVariable(Descriptor desc, Expression expr) {
		varToVal.put(desc, exprToVal.get(expr));
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
			VariableDescriptor desc = VariableDescriptor.create("t"+tempVarNonce, expr.getType(), false);
			varToVal.put(desc, tempVarNonce);
			exprToVal.put(expr, tempVarNonce);
			exprToTemp.put(expr, desc);
			tempVarNonce++;
			return true;
		}
	}
}
