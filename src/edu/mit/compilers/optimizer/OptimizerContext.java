package edu.mit.compilers.optimizer;

import java.util.HashMap;
import java.util.List;

import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.highir.descriptor.Descriptor;
import edu.mit.compilers.highir.descriptor.VariableDescriptor;
import edu.mit.compilers.highir.nodes.Expression;
import edu.mit.compilers.highir.nodes.Type;

public class OptimizerContext {
	private int tempVarNonce;

	private HashMap<Descriptor, Integer> varToVal = new HashMap<>();

	private HashMap<Expression, Integer> exprToVal = new HashMap<>();
	private HashMap<Expression, VariableDescriptor> exprToTemp = new HashMap<>();
	
	public HashMap<Descriptor, Integer> getVarToVal() {
		return varToVal;
	}

	public HashMap<Expression, Integer> getExprToVal() {
		return exprToVal;
	}

	public HashMap<Expression, VariableDescriptor> getExprToTemp() {
		return exprToTemp;
	}
	
	public void addVariable(Descriptor desc, Expression expr) {
		varToVal.put(desc, exprToVal.get(expr));
	}
	
	public VariableDescriptor addExpression(Expression expr) {
		VariableDescriptor desc = VariableDescriptor.create("t"+tempVarNonce, expr.getType(), false);
		varToVal.put(desc, tempVarNonce);
		exprToVal.put(expr, tempVarNonce);
		exprToTemp.put(expr, desc);
		tempVarNonce++;
		return desc;
	}
}
