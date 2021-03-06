package edu.mit.compilers.optimizer;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.BitSet;
import java.util.List;
import java.util.ArrayList;

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
	private int globalAssignStmtCount = 0;
	private List<BasicBlock> globalBlocks = new ArrayList<>();
	private HashMap<Optimizable, Integer> globalAssignStmtToInt = new HashMap<>();
	private HashMap<Integer, Optimizable> globalIntToAssignStmt = new HashMap<>();
	private HashMap<VariableDescriptor, Set<Integer>> globalVarToDefs = new HashMap<>();

	private int assignStmtCount = 0;
	private HashMap<Optimizable, Integer> assignStmtToInt = new HashMap<>();
	private HashMap<Integer, Optimizable> intToAssignStmt = new HashMap<>();
	//info per bb
	private HashMap<BasicBlock, HashMap<Integer, Optimizable>> bbIntToAss = new HashMap<>();
	private HashMap<BasicBlock, HashMap<Optimizable, Integer>> bbAssToInt = new HashMap<>();
	private HashMap<BasicBlock, HashMap<VariableDescriptor, Set<Integer>>> bbVarToDefs = new HashMap<>();;
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
	//info per bb
	private HashMap<BasicBlock, HashMap<VariableDescriptor, Integer>> bbLivVarToInt = new HashMap<>();
	private HashMap<BasicBlock, HashMap<Integer, VariableDescriptor>> bbLivIntToVar = new HashMap<>();
	//bitsets
	private HashMap<BasicBlock, BitSet> livIn = new HashMap<>();
	private HashMap<BasicBlock, BitSet> livOut = new HashMap<>();
	private HashMap<BasicBlock, BitSet> livDef = new HashMap<>();
	private HashMap<BasicBlock, BitSet> livUse = new HashMap<>();

	//global cp
	private BasicBlock currentBlock;
	//Loop Opt
	private AssignStmt currentStmt;
	private Set<BasicBlock> currentLoop = new HashSet<>();
	private Set<AssignStmt> invariantStmts = new HashSet<>();
	private boolean checkingLocation;


	public int getGlobalAssignStmtCount(){
		return globalAssignStmtCount;
	}

	public int incrementGlobalAssignStmtCount(){
		globalAssignStmtCount++;
		return globalAssignStmtCount;
	}

	public HashMap<Optimizable, Integer> getGlobalAssignStmtToInt(){
		return globalAssignStmtToInt;
	}

	public HashMap<Integer, Optimizable> getGlobalIntToAssignStmt(){
		return globalIntToAssignStmt;
	}

	public HashMap<VariableDescriptor, Set<Integer>> getGlobalVarToDefs(){
		return globalVarToDefs;
	}

	public Set<AssignStmt> getInvariantStmts(){
		return invariantStmts;
	}
	
	public void setCurrentLoop(Set<BasicBlock> loop){
		this.currentLoop = loop;
	}

	public void setCurrentStmt(AssignStmt stmt){
		this.currentStmt = stmt;
	}

	public AssignStmt getCurrentStmt(){
		return currentStmt;
	}

	public void setCheckingLocation(boolean checkingLocation){
		this.checkingLocation = checkingLocation;
	}

	public Set<BasicBlock> getCurrentLoop(){
		return currentLoop;
	}

	public List<BasicBlock> getGlobalBlocks(){
		return globalBlocks;
	}

	//for global cp 
	public Set<Long> getReachingConstants(VariableDescriptor var){
		Set<Long> consts = new HashSet<>();

		//do we have info on this block?
		if(!getBbVarToDefs().containsKey(getCurrentBlock())){
			return new HashSet<>(); //if we don't, we can't do anything
		}

		//do we have info about this variable?
		if(getBbVarToDefs().get(getCurrentBlock()).containsKey(var)){

			for(Integer def : getBbVarToDefs().get(getCurrentBlock()).get(var)){
				//is this definition alive?
				if(getRdIn().containsKey(getCurrentBlock())){
					if(getRdIn().get(getCurrentBlock()).get(def)){
						//does it assign var to const?
						Optimizable stmt = getBbIntToAss().get(getCurrentBlock()).get(def);
						//if it was a method arg
						if(stmt instanceof VariableDescriptor){
							continue;
						}

						AssignStmt assignStmt = (AssignStmt)stmt;

						if(assignStmt.assignsToConstant()){
							consts.add(assignStmt.whatConst());
						} else {
							//NOT ALL ARE CONSTANTS! Return empty set
							return new HashSet<>();
						}
					}						
				}
			}
		}

		return consts;
	}

	//for loop invariant motion
	//note: must set currentBlock and currentLoop
	//note: if RD is the current stmt we're evaluating, it does not count
	public boolean areRDsOutsideLoop(VariableDescriptor var){
		//System.out.println("In OptCtx, checking RDs for " + var + " in " + getCurrentBlock());

		if(!rdIn.containsKey(currentBlock) || !bbVarToDefs.get(currentBlock).containsKey(var)){
			return false;
		}
		BitSet in = rdIn.get(currentBlock);

		//System.out.println("rdIn[" + currentBlock + "] =" + in);

		Set<Integer> defsForVar = bbVarToDefs.get(currentBlock).get(var);
		for(Integer def : defsForVar){

			//todo: if def corresponds to this.currentStmt, continue
			//but ONLY if we are checking location of assignStmt, not an expression
			Optimizable stmtOfDef = bbIntToAss.get(currentBlock).get(def);
			if(stmtOfDef.equals(currentStmt) && checkingLocation){
				continue;
			}

			if(in.get(def)){ //this definition reaches loop
				if(isDefInLoop(def)){
					return false;
				}
			}
		}
		return true;
	}

	public boolean isDefInLoop(Integer def){
		Optimizable stmt = bbIntToAss.get(currentBlock).get(def);
		for(BasicBlock block : currentLoop){
			if(block.isDefInLoop(stmt)){
				return true;
			}
		}
		return false;
	}

	public HashMap<BasicBlock, HashMap<VariableDescriptor, Integer>> getBbLivVarToInt(){
		return bbLivVarToInt;
	}

	public HashMap<BasicBlock, HashMap<Integer, VariableDescriptor>> getBbLivIntToVar(){
		return bbLivIntToVar;
	}

	public boolean isLiveIn(BasicBlock bb, VariableDescriptor var){
		if(bbLivVarToInt.containsKey(bb)){
			if(bbLivVarToInt.get(bb).containsKey(var)){
				Integer i = bbLivVarToInt.get(bb).get(var);
				return livIn.get(bb).get(i);
			}
		}
		return false; //note: will return false if bb is global, enterBlock, or LeaveBlock
	}

	public boolean isLiveOut(BasicBlock bb, VariableDescriptor var){
		if(bbLivVarToInt.containsKey(bb)){
			if(bbLivVarToInt.get(bb).containsKey(var)){
				Integer i = bbLivVarToInt.get(bb).get(var);
				return livOut.get(bb).get(i);
			}
		}
		return false; //note: will return false if bb is global, enterBlock, or LeaveBlock
	}

	public HashMap<BasicBlock, HashMap<VariableDescriptor, Set<Integer>>> getBbVarToDefs(){
		return bbVarToDefs;
	}

	public HashMap<BasicBlock, HashMap<Optimizable, Integer>> getBbAssToInt(){
		return bbAssToInt;
	}

	public HashMap<BasicBlock, HashMap<Integer, Optimizable>> getBbIntToAss(){
		return bbIntToAss;
	}

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

	public HashMap<Integer, Optimizable> getIntToAssignStmt(){
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
		for(Optimizable stmt : getAssignStmtToInt().keySet()){
			s += stmt.toString() + ": ";
			s += getAssignStmtToInt().get(stmt).toString() + "\n";
		}
		return s;
	}

	public HashMap<Optimizable, Integer> getAssignStmtToInt(){
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
		assignStmtCount = globalAssignStmtCount;
	}

	public void setGlobalAssignStmtCount(int count){
		globalAssignStmtCount = count;
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
