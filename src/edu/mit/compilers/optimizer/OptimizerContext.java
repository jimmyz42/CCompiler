package edu.mit.compilers.optimizer;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
	
	// Common Subexpression Elimination Maps
	// Temps declared in method so far
	private Set<Location> cseDeclaredTemps = new HashSet<>();
	// Set of expressions containing a given variable
	private HashMap<Location, Set<Expression>> exprsContainingVar = new HashMap<>();
	// Expressions generated
	private Set<Expression> cseGenExprs = new HashSet<>();
	// Locations re-assigned (thus killing any expressions containing it
	// that are not re-generated)
	private Set<Location> cseKillVars = new HashSet<>();
	private Set<Expression> cseAvailableExprs = new HashSet<>();

	//Copy Propagation Maps
	private HashMap<Location, Location> cpTempToVar = new HashMap<>();
	private HashMap<Location, Set<Location>> cpVarToSet = new HashMap<>();

	//Constant Propagation Maps
	private HashMap<Location, Literal> varToConst = new HashMap<>();

	//reaching definitions:
	//numbering definitions
	private int assignStmtCount = 0;
	private HashMap<AssignStmt, Integer> assignStmtToInt = new HashMap<>();
	private HashMap<Integer, AssignStmt> intToAssignStmt = new HashMap<>();

	private HashMap<BasicBlock, HashMap<AssignStmt, Integer>> bbInfo;
	//finding what defs map to a var
	private HashMap<VariableDescriptor, Set<Integer>> varToDefs = new HashMap<>();
	//bitsets 
	//NOTE: each index for a bitset corresponds to an assignStmt in that method. To 
	//	find which assignStmt the bit correspondes to, you must re-run "number definitions"
	//	in Optimizer. Then that info will be stored in assignStmtToInt
	private HashMap<BasicBlock, BitSet> rdIn = new HashMap<>();
	private HashMap<BasicBlock, BitSet> rdOut = new HashMap<>();
	private HashMap<BasicBlock, BitSet> rdGen = new HashMap<>();
	private HashMap<BasicBlock, BitSet> rdKill = new HashMap<>();
	
	//liveness:
	//numbering variables
	private int varCount = 0;
	private HashMap<VariableDescriptor, Integer> livVarToInt = new HashMap<>();
	private HashMap<Integer, VariableDescriptor> livIntToVar = new HashMap<>();
	//bitsets
	private HashMap<BasicBlock, BitSet> livIn = new HashMap<>();
	private HashMap<BasicBlock, BitSet> livOut = new HashMap<>();
	private HashMap<BasicBlock, BitSet> livDef = new HashMap<>();
	private HashMap<BasicBlock, BitSet> livUse = new HashMap<>();

	//global cp
	private BasicBlock currentBlock;

	public HashMap<BasicBlock, BitSet> getLivIn(){
		return livIn;
	}

	public HashMap<BasicBlock, BitSet> getLivOut(){
		return livOut;
	}

	public HashMap<BasicBlock, BitSet> getLivUse(){
		return livUse;
	}

	public HashMap<BasicBlock, BitSet> getLivDef(){
		return livDef;
	}

	public void resetVarCount(){
		varCount = 0;
	}

	public int incrementVarCount(){
		varCount++;
		return varCount;
	}

	public int getVarCount(){
		return varCount;
	}

	public HashMap<VariableDescriptor, Integer> getLivVarToInt(){
		return livVarToInt;
	}

	public HashMap<Integer, VariableDescriptor> getLivIntToVar(){
		return livIntToVar;
	}

	public void setCurrentBlock(BasicBlock block){
		currentBlock = block;
	}

	public BasicBlock getCurrentBlock(){
		return currentBlock;
	}

	public HashMap<Integer, AssignStmt> getIntToAssignStmt(){
		return intToAssignStmt;
	}

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
	
	public int getNumberOfTemps() {
		return tempVarNonce;
	}

	public Set<Location> getCSEDeclaredTemps() {
		return cseDeclaredTemps;
	}
	
	public Set<Expression> getCSEGenExprs() {
		return cseGenExprs;
	}

	public Set<Location> getCSEKillVars() {
		return cseKillVars;
	}
	
	public Set<Expression> getCSEAvailableExprs() {
		return cseAvailableExprs;
	}
	
	public Set<Expression> getExprsContainingVar(Location loc) {
		if(exprsContainingVar.containsKey(loc)) {
			return exprsContainingVar.get(loc);
		} else {
			return Collections.emptySet();
		}
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
		// Clone expression so copy in HashSets/Maps doesn't get modified
		expr = expr.clone();
		if(expr instanceof Literal || expr instanceof Location) {
			return false; // don't do CSE for constants/variables
		}
		cseGenExprs.add(expr);
		
		// Always add temp assignment statement, but for each expr use same temp
		// each time. So when variables are reassigned, we are safe (always return true)
		// CSE will eliminate the extraneous assignments
		if(!exprToVal.containsKey(expr)) {
			VariableDescriptor desc = VariableDescriptor.create("t"+tempVarNonce, expr.getType(), false);
			desc.setToTemp();
			IdLocation loc = IdLocation.create(desc);
			varToVal.put(loc, tempVarNonce);
			exprToVal.put(expr, tempVarNonce);
			exprToTemp.put(expr, loc);
			tempVarNonce++;
			
			for(Location var: expr.getLocationsUsed()) {
				if(!exprsContainingVar.containsKey(var)) {
					exprsContainingVar.put(var, new HashSet<Expression>());
				}
				exprsContainingVar.get(var).add(expr);
			}
		}
		return true;
	}
}
