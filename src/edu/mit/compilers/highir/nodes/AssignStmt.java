package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.BitSet;

import edu.mit.compilers.cfg.CFGContext;
import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.highir.descriptor.Descriptor;
import edu.mit.compilers.highir.descriptor.VariableDescriptor;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.Register;
import edu.mit.compilers.lowir.instructions.Mov;
import edu.mit.compilers.optimizer.Optimizable;
import edu.mit.compilers.optimizer.OptimizerContext;
import exceptions.TypeMismatchError;

public class AssignStmt extends Statement implements Optimizable {
	private Location location;
	private Expression expression;
	private String assignOp;
	private int number;

	public AssignStmt(Location location, String assignOp, Expression expression) {
		this.location = location;
		this.assignOp = assignOp;
		this.expression = expression;
	}

	public Boolean assignsToConstant(){
		if(expression instanceof IntLiteral){
			return true;
		}
		return false;
	}

	public long whatConst(){
		if(this.assignsToConstant()){
			IntLiteral intLit = (IntLiteral)expression;
			return intLit.getValue();
		} else {
			//error
			System.out.println("This AssignStmt does not assign to a constant.");
		}
		return -1;
	}

	public Location getLocation(){
		return location;
	}

	public static AssignStmt create(DecafSemanticChecker checker, DecafParser.AssignStmtContext ctx) {
		Location location = Location.create(checker, ctx.location());
		Expression expression = Expression.create(checker, ctx.expr());
		String assignOp = ctx.assign_op().getText();
		return create(location, assignOp, expression, ctx);
	}

	public static AssignStmt create(Location location, String assignOp, Expression expression, DecafParser.StatementContext ctx) {
		if (location.getType() != expression.getType()) {
			throw new TypeMismatchError("Two sides of an assignment should have the same type", ctx);
		}
		if (!(location.getType() instanceof ScalarType)) {
			throw new TypeMismatchError("Can only assign a scalar", ctx);
		}
		if (!assignOp.equals("=")) {
			if (location.getType() != ScalarType.INT) {
				throw new TypeMismatchError("Can only use += and -= on ints", ctx);
			}
			if (assignOp.equals("+=")) {
				expression = new AddOpExpr(new AddOp("+"), location, expression);
			} else if (assignOp.equals("-=")) {
				expression = new AddOpExpr(new AddOp("-"), location, expression);
			}
		}

		return new AssignStmt(location, "=", expression);
	}

	public static AssignStmt create(Location location, String assignOp, Expression expression) {
		return new AssignStmt(location, "=", expression);
	}

	@Override
	public void prettyPrint(PrintWriter pw, String prefix) {
		super.prettyPrint(pw, prefix);
		pw.println(prefix + "-location:");
		location.prettyPrint(pw, prefix + "    ");
		pw.println(prefix+ "-operator: " + assignOp);
		pw.println(prefix + "-expression:");
		expression.prettyPrint(pw, prefix + "    ");
	}

	@Override
	public void cfgPrint(PrintWriter pw, String prefix) {
		pw.print(prefix);
		location.cfgPrint(pw,"");
		pw.print(" " + assignOp);
		expression.cfgPrint(pw," ");
		pw.println();
	}

	@Override
	public CFG generateCFG(CFGContext context) {
		return BasicBlock.create(this);
	}

	@Override
	public void generateAssembly(AssemblyContext ctx){
		expression.generateAssembly(ctx);
		location.generateAssembly(ctx);

		Register src = expression.allocateRegister(ctx);
		ctx.addInstruction(Mov.create(src, location.getLocation(ctx)));
		ctx.deallocateRegister(src);
	}

	@Override
	public long getNumStackAllocations() {
		return expression.getNumStackAllocations() + location.getNumStackAllocations();
	}

	public boolean isReflexive() {
		return location == expression;
	}

	@Override
	public Set<Descriptor> getConsumedDescriptors() {
		return expression.getConsumedDescriptors();
	}

	@Override
	public Set<Descriptor> getGeneratedDescriptors() {
		return location.getGeneratedDescriptors();
	}

	@Override
	public Optimizable doAlgebraicSimplification() {
		this.expression = (Expression) expression.doAlgebraicSimplification();
		return this;
	}

	@Override
	public boolean isLinearizable() {
		return false;
	}

	@Override
	public List<Optimizable> generateTemporaries(OptimizerContext context, boolean skipGeneration) {

		List<Optimizable> temps = new ArrayList<>();
		temps.addAll(location.generateTemporaries(context, false));
		temps.addAll(expression.generateTemporaries(context, true));
		temps.add(this);

		if(expression.isLinearizable()) {
			if(context.addExpression(expression)) {
				Location temp = context.getExprToTemp().get(expression);
				if(!context.getCSEDeclaredTemps().contains(temp)) {
					temps.add(temp.getVariable());
					context.getCSEDeclaredTemps().add(temp);
				}
				temps.add(AssignStmt.create(temp, assignOp, location));
			}
		}
		
		// FOR CSE 
		context.getCSEKillVars().add(location);
		context.getCSEGenExprs().removeAll(context.getExprsContainingVar(location));
		
		return temps;
	}

	@Override
	public void doConstantPropagation(OptimizerContext ctx){
		if(location.getVariable().isGlobal)
			return;
		//if variable is being assigned OR reassigned to a constant, update map
		if(expression instanceof Literal){ //variable is being assigned to a constant
			ctx.getVarToConst().put(location, (Literal)expression);
		}else if (ctx.getVarToConst().containsKey(location)){ //if variable is in our map, but not getting reassigned to a constant, remove from map
			ctx.getVarToConst().remove(location);
		}

		//if expression is a variable, check to see if variable is in map, replace
		if(expression instanceof IdLocation){
			Location exprLoc = (Location)expression;
			if(ctx.getVarToConst().containsKey(exprLoc)){
				expression = ctx.getVarToConst().get(exprLoc);
			}
		}

		expression.doConstantPropagation(ctx);
	}

	@Override
	public void doGlobalConstantPropagation(OptimizerContext ctx){
		expression.doGlobalConstantPropagation(ctx);
	}

	@Override
	public void doCopyPropagation(OptimizerContext ctx){
		if(location.getVariable().isGlobal)
			return;
		
		Location var;
		if(expression instanceof Location) {
			var = (Location) expression;
		} else {
			expression.doCopyPropagation(ctx);
			var = location;
		}

		if(ctx.getCPVarToSet().containsKey(location)) {
			for(Location loc: ctx.getCPVarToSet().get(location)) {
				ctx.getCPTempToVar().remove(loc);
			}
		} else {
			ctx.getCPVarToSet().put(location, new HashSet<Location>());
		}


		if(ctx.getCPVarToSet().containsKey(var)) {
			for(Location loc: ctx.getCPVarToSet().get(var)) {
				ctx.getCPTempToVar().remove(loc);
			}
		} else {
			ctx.getCPVarToSet().put(var, new HashSet<Location>());
		}
		
		ctx.getCPVarToSet().get(var).add(location);
		ctx.getCPTempToVar().put(location, var);
	}
	
	@Override
	public void findVarToDefs(OptimizerContext ctx){
		VariableDescriptor var = location.getVariable();
		if(ctx.getVarToDefs().containsKey(var)){
			//already exists, so add num to set
			ctx.getVarToDefs().get(var).add(this.number);
		} else {
			//doesn't exists yet, so put
			ctx.getVarToDefs().put(var, new HashSet<>(Arrays.asList(this.number)));
		}
		
	}

	@Override
	public void makeUseSet(OptimizerContext ctx, BitSet use){
		expression.makeUseSet(ctx, use);
	}

	public void makeDefSet(OptimizerContext ctx, BitSet defSet){
		VariableDescriptor var = location.getVariable();
		if(ctx.getLivVarToInt().containsKey(var)){
			Integer i = ctx.getLivVarToInt().get(var);
			defSet.set(i);
		}

	}

	public void makeGenSet(OptimizerContext ctx, BitSet genSet){
		//have we already set a gen for this variable? if so, 0.
		Set<Integer> defsForVar = ctx.getVarToDefs().get(this.location.getVariable());
		for(Integer def : defsForVar){
			//if another bit for this variable is already set to true, we set this one to zero
			//because we are iterating backwards through the bb components
			if(genSet.get(def)){ 
				return;
			}
		}

		genSet.set(this.number);
	}

	public void makeKillSet(OptimizerContext ctx, BitSet killSet){
		//killset: bit is 1 if def is killed 
		Set<Integer> defsForVar = ctx.getVarToDefs().get(this.location.getVariable());
		for (Integer def : defsForVar){
			if (def != this.number){ //def is not for THIS statement 
				killSet.set(def);
			}
		}
	}

	@Override
	public void numberDefinitions(OptimizerContext ctx){
		int count = ctx.incrementAssignStmtCount();
		this.number = count;
		ctx.getAssignStmtToInt().put(this, count);
	}

	public int getNumber(){
		return number;
	}

	@Override
	public void doCSE(OptimizerContext ctx) {
		// Save original expression since it may be modified by
		// the recursive doCSE calls 
		Expression origExpr = expression.clone();
		if(ctx.getCSEAvailableExprs().contains(expression) 
				&& ctx.getExprToTemp().get(expression) != null) {
			expression = ctx.getExprToTemp().get(expression);
		} else {
			expression.doCSE(ctx);
		}
		ctx.getCSEAvailableExprs().removeAll(ctx.getExprsContainingVar(location));
		ctx.getCSEAvailableExprs().add(origExpr);
	}
}
