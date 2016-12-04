package edu.mit.compilers.optimizer;

import java.util.HashMap;
import java.util.Set;
import java.util.BitSet;

import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.highir.descriptor.VariableDescriptor;
import edu.mit.compilers.highir.nodes.Expression;
import edu.mit.compilers.highir.nodes.IdLocation;
import edu.mit.compilers.highir.nodes.Literal;
import edu.mit.compilers.highir.nodes.Location;
import edu.mit.compilers.highir.nodes.AssignStmt;

public class OptimizerContext {
	private int tempVarNonce;

	private HashMap<Location, Integer> varToVal = new HashMap<>();
	private HashMap<Expression, Integer> exprToVal = new HashMap<>();
	private HashMap<Expression, Location> exprToTemp = new HashMap<>();

	private HashMap<Expression, Location> cseExprToVar = new HashMap<>();
	private HashMap<Location, Set<Expression>> cseVarToExprs = new HashMap<>();

	//Copy Propagation Maps
	private HashMap<Location, Location> cpTempToVar = new HashMap<>();
	private HashMap<Location, Set<Location>> cpVarToSet = new HashMap<>();

	//Constant Propagation Maps
	private HashMap<Location, Literal> varToConst = new HashMap<>();

	//reaching definitions:
	//numbering definitions
	private int assignStmtCount = 0;
	private HashMap<AssignStmt, Integer> assignStmtToInt = new HashMap<>();
	//finding what defs map to a var
	private HashMap<VariableDescriptor, Set<Integer>> varToDefs = new HashMap<>();
	//bitsets 
	private HashMap<BasicBlock, BitSet> rdIn = new HashMap<>();
	private HashMap<BasicBlock, BitSet> rdOut = new HashMap<>();
	private HashMap<BasicBlock, BitSet> rdGen = new HashMap<>();
	private HashMap<BasicBlock, BitSet> rdKill = new HashMap<>();


	public HashMap<VariableDescriptor, Set<Integer>> getVarToDefs(){
		return varToDefs;
	}

	public HashMap<BasicBlock, BitSet> getRdIn(){
		return rdIn;
	}
	
	public HashMap<BasicBlock, BitSet> getRdOut(){
		return rdOut;
	}

	public HashMap<BasicBlock, BitSet> getRdGen(){
		return rdGen;
	}

	public HashMap<BasicBlock, BitSet> getRdKill(){
		return rdKill;
	}

	public String prettyPrintVarToDefs(){
		String s = "";
		for(VariableDescriptor var : getVarToDefs().keySet()){
			s += var.getName() + ": ";
			s += getVarToDefs().get(var).toString() + "\n";
		}
		return s;
	}
	
	public String prettyPrintAssignStmtToInt(){
		String s = "";
		for(AssignStmt stmt : getAssignStmtToInt().keySet()){
			s += stmt.toString() + ": ";
			s += getAssignStmtToInt().get(stmt).toString() + "\n";
		}
		return s;
	}

	public HashMap<AssignStmt, Integer> getAssignStmtToInt(){
		return assignStmtToInt;
	}	

	public int getAssignStmtCount(){
		return assignStmtCount;
	}

	public int incrementAssignStmtCount(){
		assignStmtCount = assignStmtCount + 1;
		return assignStmtCount;
	}

	public void resetAssignStmtCount(){
		assignStmtCount = 0;
	}

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
		VariableDescriptor desc = VariableDescriptor.create("t"+tempVarNonce, expr.getType(), false);;
		desc.setToTemp();
		IdLocation loc = IdLocation.create(desc);
		varToVal.put(loc, tempVarNonce);
		exprToVal.put(expr, tempVarNonce);
		exprToTemp.put(expr, loc);
		tempVarNonce++;
		return true;
	}
}
