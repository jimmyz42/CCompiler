package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.BitSet;
import java.util.ArrayList;

import edu.mit.compilers.cfg.CFGContext;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.highir.descriptor.ArrayVariableDescriptor;
import edu.mit.compilers.highir.descriptor.Descriptor;
import edu.mit.compilers.highir.descriptor.VariableDescriptor;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.ImmediateValue;
import edu.mit.compilers.lowir.Memory;
import edu.mit.compilers.lowir.Register;
import edu.mit.compilers.lowir.Storage;
import edu.mit.compilers.lowir.instructions.Cmp;
import edu.mit.compilers.lowir.instructions.Jae;
import edu.mit.compilers.lowir.instructions.Sub;
import edu.mit.compilers.lowir.instructions.Xor;
import edu.mit.compilers.optimizer.Optimizable;
import edu.mit.compilers.optimizer.OptimizerContext;
import exceptions.TypeMismatchError;
import exceptions.UndeclaredIdentifierError;

public class ArrayLocation extends Location {
	private Expression index;

	public ArrayLocation(ArrayVariableDescriptor variable, Expression index) {
		super(variable);
		this.index = index;
	}

	@Override
	public Type getType() {
		return ((ArrayType) getVariable().getType()).getElementType();
	}

	@Override
	public ArrayVariableDescriptor getVariable() {
		return (ArrayVariableDescriptor) super.getVariable();
	}

	@Override
	public void prettyPrint(PrintWriter pw, String prefix) {
		super.prettyPrint(pw, prefix);
		pw.println(prefix + getVariable().getName() + "[");
		index.prettyPrint(pw, prefix + "  ");
		pw.println(prefix + "] ");
	}

	@Override
	public void cfgPrint(PrintWriter pw, String prefix) {
		pw.print(prefix + getVariable().getName() + "[");
		index.cfgPrint(pw,"");
		pw.print("]");
	}

	@Override
	public CFG generateCFG(CFGContext context) {
		return index.generateCFG(context);
	}

	@Override
	public void generateAssembly(AssemblyContext ctx) {
		index.generateAssembly(ctx);
	}

	@Override
	public long getNumStackAllocations() {
		return index.getNumStackAllocations();
	}

	public Storage getLocation(AssemblyContext ctx) {
		Register src = index.allocateRegister(ctx);

		ctx.addInstruction(Cmp.create(ImmediateValue.create(variable.getType().getLength()),src));
		ctx.addInstruction(Jae.create(Memory.create("array_index_error")));
		ctx.addInstruction(Xor.create(Register.create("%rax"), Register.create("%rax")));
		ctx.addInstruction(Sub.create(src, Register.create("%rax")));

		Storage result =  getVariable().getLocation(ctx, Register.create("%rax"));
		ctx.deallocateRegister(src);
		return result;
	}

	public Storage getLocation(AssemblyContext ctx, boolean forceStackLocation) {
		if(forceStackLocation) {
			Register reg = allocateRegister(ctx);
			ctx.storeStack(getStorageTuple(), reg);
			ctx.deallocateRegister(reg);
			return ctx.getStackLocation(getStorageTuple());
		} else {
			return getLocation(ctx);
		}
	}

	@Override
	public Register allocateRegister(AssemblyContext ctx) {
		Register src = index.allocateRegister(ctx);

		ctx.addInstruction(Cmp.create(ImmediateValue.create(variable.getType().getLength()),src));
		ctx.addInstruction(Jae.create(Memory.create("array_index_error")));
		ctx.addInstruction(Xor.create(Register.create("%rax"), Register.create("%rax")));
		ctx.addInstruction(Sub.create(src, Register.create("%rax")));

		Register result = getVariable().allocateRegister(ctx, Register.create("%rax"));
		ctx.deallocateRegister(src);
		return result;
	}

	@Override
	public boolean isInvariantStmt(OptimizerContext ctx){
		if(index.isInvariantStmt(ctx)){
			return true;
		}
		return false;
	}

	@Override
	public Set<Descriptor> getConsumedDescriptors() {
		Set<Descriptor> consumed = new HashSet<>();
		consumed.add(variable);
		consumed.addAll(index.getConsumedDescriptors());
		return consumed;
	}

	@Override
	public Set<Descriptor> getGeneratedDescriptors() {
		return Collections.singleton((Descriptor)variable);
	}

	@Override
	public boolean isLinearizable() {
		return index.isLinearizable();
	}

	@Override
	public List<Optimizable> generateTemporaries(OptimizerContext context, boolean skipGeneration) {
		return index.generateTemporaries(context, false);
	}

    @Override
    public void doCopyPropagation(OptimizerContext ctx){
		if(index instanceof Location){
			Location loc = (Location) index;
			if(ctx.getCPTempToVar().containsKey(loc)){
				Location var = ctx.getCPTempToVar().get(loc);
				index = var; //put var there instead of temp
			}
		} else {
			index.doCopyPropagation(ctx);
		}
    }

    @Override
    public void doConstantPropagation(OptimizerContext ctx){
		if(index instanceof Location){
			Location loc = (Location) index;
			//is it in the map?
			if(ctx.getVarToConst().containsKey(loc)){
				index = ctx.getVarToConst().get(loc); //replace var with const
			}
		} else
			index.doConstantPropagation(ctx);
    }

    @Override
    public void makeUseSet(OptimizerContext ctx, BitSet use){
    	index.makeUseSet(ctx, use);
    }

    @Override
    public void doGlobalConstantPropagation(OptimizerContext ctx){
    	if(index instanceof Location){
			Location indexLoc = (Location)index;
			VariableDescriptor var = indexLoc.getVariable();
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
							Optimizable stmt = ctx.getIntToAssignStmt().get(def);
							//if it was a method arg
							if(stmt instanceof VariableDescriptor){
								continue;
							}

							AssignStmt assignStmt = (AssignStmt)stmt;

							if(assignStmt.assignsToConstant()){
								consts.add(assignStmt.whatConst());
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
					index = new IntLiteral(consts.get(0));
				}
			}
    	} else {
    		index.doGlobalConstantPropagation(ctx);
    	}
    }

	@Override
	public int hashCode() {
		return ("arraylocation" + variable.hashCode() + index.hashCode()).hashCode();
	}

	public static ArrayLocation create(DecafSemanticChecker checker, DecafParser.ArrayLocationContext ctx) {
		String varName = ctx.ID().getText();
		VariableDescriptor variable = checker.currentSymbolTable().getVariable(varName, ctx);
		Expression index = Expression.create(checker, ctx.expr());

		if (variable == null) {
			throw new UndeclaredIdentifierError("Array is not declared", ctx);
		}
		if (!(variable.getType() instanceof ArrayType)) {
			throw new TypeMismatchError("Can only index variables", ctx);
		}
		if (index.getType() != ScalarType.INT) {
			throw new TypeMismatchError("Array index must be an int", ctx.expr());
		}

		return new ArrayLocation((ArrayVariableDescriptor) variable, index);
	}
}
