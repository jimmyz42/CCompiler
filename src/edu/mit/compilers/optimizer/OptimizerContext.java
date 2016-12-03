package edu.mit.compilers.optimizer;

import java.util.Collections;
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
