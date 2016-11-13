package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import edu.mit.compilers.cfg.CFGAble;
import edu.mit.compilers.cfg.CFGContext;
import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.cfg.components.EnterBlock;
import edu.mit.compilers.cfg.components.LeaveBlock;
import edu.mit.compilers.cfg.components.NonVoidBlock;
import edu.mit.compilers.grammar.DecafParser.Extern_argContext;
import edu.mit.compilers.grammar.DecafParser.Method_callContext;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.highir.descriptor.*;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.ImmediateValue;
import edu.mit.compilers.lowir.Memory;
import edu.mit.compilers.lowir.Register;
import edu.mit.compilers.lowir.instructions.Add;
import edu.mit.compilers.lowir.instructions.Call;
import edu.mit.compilers.lowir.instructions.Instruction;
import edu.mit.compilers.lowir.instructions.Lea;
import edu.mit.compilers.lowir.instructions.Mov;
import edu.mit.compilers.lowir.instructions.Pop;
import edu.mit.compilers.lowir.instructions.Push;
import edu.mit.compilers.lowir.instructions.Sub;
import edu.mit.compilers.lowir.instructions.Xor;
import exceptions.MethodCallException;
import exceptions.UndeclaredIdentifierError;

public class MethodCallExpr extends Expression {
	private final FunctionDescriptor function;
	private final List<ExternArg> arguments;

	public MethodCallExpr(FunctionDescriptor function, List<ExternArg> arguments) {
		this.function = function;
		this.arguments = Collections.unmodifiableList(arguments);
	}

	@Override
	public Type getType() {
		return function.getType();
	}

	public static MethodCallExpr create(DecafSemanticChecker checker, Method_callContext ctx) {
		String functionName = ctx.ID().getText();
		FunctionDescriptor function = checker.currentSymbolTable().getFunction(functionName, ctx);

		List<ExternArg> arguments = new ArrayList<>();
		for (Extern_argContext argument : ctx.extern_arg()) {
			arguments.add(ExternArg.create(checker, argument));
		}

		if (function == null) {
			throw new UndeclaredIdentifierError("Tried to call an undeclared function", ctx);
		}

		if (function instanceof MethodDescriptor) {
			MethodDescriptor method = (MethodDescriptor) function;
			List<VariableDescriptor> expectedArgs = method.getArguments();
			if (arguments.size() != expectedArgs.size()) {
				throw new MethodCallException("Expected " + expectedArgs.size() + " arguments, got " + arguments.size(), ctx);
			}
			for (int i = 0; i < arguments.size() && i <= Integer.MAX_VALUE; i++) {
				if (!(arguments.get(i) instanceof Expression)) {
					throw new MethodCallException("Can't have string literals as method arguments", ctx.extern_arg(i));
				}
				Expression arg = (Expression) arguments.get(i);
				if (arg.getType() != expectedArgs.get(i).getType()) {
					// 1-based indexing for arguments in error messages
					throw new MethodCallException("Argument #" + (i + 1) + " has type " + arg.getType() +
							", expected " + expectedArgs.get(i).getType(), ctx.extern_arg(i));
				}
			}
		}

		return new MethodCallExpr(function, arguments);
	}

	@Override
	public BasicBlock shortCircuit(CFG trueBranch, CFG falseBranch) {
		EqOpExpr expr = new EqOpExpr(new EqOp("=="), this, new BoolLiteral(true));
		return expr.shortCircuit(trueBranch, falseBranch);
	}

	@Override
	public void prettyPrint(PrintWriter pw, String prefix) {
		super.prettyPrint(pw, prefix);
		pw.print(prefix + "-function descriptor: ");
		function.getType().prettyPrint(pw, "");
		pw.print(" ");
		pw.println(function.getName());
		pw.println(prefix + "-passed in arguments:");
		for (ExternArg arg : arguments){
			arg.prettyPrint(pw, prefix+"    ");
		}
	}

	@Override
	public void cfgPrint(PrintWriter pw, String prefix) {
		pw.print(prefix + function.getName() + "(");
		for(Iterator<ExternArg> it = arguments.iterator(); it.hasNext();) {
			it.next().cfgPrint(pw, "");
			if (it.hasNext()) {
				pw.print(", ");
			}
		}
		pw.print(")");
	}

	@Override
	public CFG generateCFG(CFGContext context) {
    	VariableDescriptor temp = context.generateNewTemporary(getType());
    	List<CFGAble> components = new ArrayList<>();
    	components.add(temp);
    	components.add(AssignStmt.create(IdLocation.create(temp), "=", this));
    	BasicBlock thisCFG = BasicBlock.create(components);

		CFG firstCFG = arguments.get(0).generateCFG(context);
		CFG currentCFG = firstCFG;
		for(int i =1; i < arguments.size(); i++) {
			CFG nextCFG = arguments.get(i).generateCFG(context);
			
			currentCFG.setNextBlock(nextCFG.getEntryBlock());
			nextCFG.setPreviousBlock(currentCFG.getExitBlock());
			currentCFG = nextCFG;
		}
		
		currentCFG.setNextBlock(thisCFG.getEntryBlock());
		thisCFG.addPreviousBlock(currentCFG.getExitBlock());

		return new CFG(firstCFG.getEntryBlock(), thisCFG.getExitBlock());
	}

	@Override
	public void generateAssembly(AssemblyContext ctx) {
		List<Instruction> instructions = new ArrayList<>();
		int PARAMS_IN_REGISTER = 6;
		for(int i = 0; i < arguments.size() && i < PARAMS_IN_REGISTER; i++) {
			ExternArg node = arguments.get(i);
			node.generateAssembly(ctx);
			switch(i) {
			case 0:
				instructions.add(Mov.create(node.getLocation(ctx, true), Register.create("%rdi")));
				break;
			case 1:
				instructions.add(Mov.create(node.getLocation(ctx, true), Register.create("%rsi")));
				break;
			case 2:
				instructions.add(Mov.create(node.getLocation(ctx, true), Register.create("%rdx")));
				break;
			case 3:
				instructions.add(Mov.create(node.getLocation(ctx, true), Register.create("%rcx")));
				break;
			case 4:
				instructions.add(Mov.create(node.getLocation(ctx, true), Register.create("%r8")));
				break;
			case 5:
				instructions.add(Mov.create(node.getLocation(ctx, true), Register.create("%r9")));
				break;
			}
		}

		//int position = 0;
		for(int i = arguments.size()-1; i >= PARAMS_IN_REGISTER; i--) {
			ExternArg node = arguments.get(i);
			node.generateAssembly(ctx);
			instructions.add(Push.create(node.getLocation(ctx, true)));
		}

		if(function instanceof ExternDescriptor) {
			instructions.add(Xor.create(Register.create("%rax"), Register.create("%rax")));
		}
		instructions.add(Call.create(Memory.create(function.getExpressionName())));

		//increase %rsp to ignore params pushed to the stack
		int paramsOnStack =Math.max(0, arguments.size() - PARAMS_IN_REGISTER);
		instructions.add(Add.create(ImmediateValue.create(paramsOnStack*8),Register.create("%rsp")));


		ctx.addInstructions(instructions);
		ctx.storeStack(getStorageTuple(), Register.create("%rax"));
	}

	@Override
	public long getNumStackAllocations() {
		int numStackAllocations = 0;
		for(ExternArg node: arguments) {
			numStackAllocations += node.getNumStackAllocations();
		}
		numStackAllocations++;
		return numStackAllocations;
	}
}