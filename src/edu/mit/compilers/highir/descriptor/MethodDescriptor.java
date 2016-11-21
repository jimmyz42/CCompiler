package edu.mit.compilers.highir.descriptor;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.mit.compilers.cfg.CFGContext;
import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.grammar.DecafParser.Method_argument_declContext;
import edu.mit.compilers.grammar.DecafParser.Method_declContext;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.highir.nodes.Block;
import edu.mit.compilers.highir.nodes.ScalarType;
import edu.mit.compilers.highir.nodes.Type;
import edu.mit.compilers.highir.nodes.VoidType;
import edu.mit.compilers.highir.symboltable.ArgumentSymbolTable;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.Register;
import edu.mit.compilers.optimizer.Optimizable;
import edu.mit.compilers.optimizer.OptimizerContext;
import exceptions.TypeMismatchError;

public class MethodDescriptor extends FunctionDescriptor {
	private final List<VariableDescriptor> arguments;
	private final Block body;

	public MethodDescriptor(String name, Type returnType, List<VariableDescriptor> arguments, Block body) {
		super(name, returnType);
		this.arguments = Collections.unmodifiableList(arguments);
		this.body = body;
	}

	public List<VariableDescriptor> getArguments() {
		return arguments;
	}

	@Override
	public String getExpressionName() {
		return getName();
	}

	public static MethodDescriptor create(DecafSemanticChecker checker, Method_declContext ctx) {
		Type returnType = ctx.type() == null ? VoidType.VOID : ScalarType.create(checker, ctx.type());
		String name = ctx.ID().getText();
		ArgumentSymbolTable argTable = new ArgumentSymbolTable(checker.currentSymbolTable());

		Block body = Block.createEmpty(argTable, false);

		List<VariableDescriptor> arguments = new ArrayList<>();
		for (Method_argument_declContext argumentDecl : ctx.method_argument_decl()) {
			Type type = ScalarType.create(checker, argumentDecl.type());
			String argName = argumentDecl.ID().getText();
			arguments.add(argTable.addVariable(type, argName, argumentDecl));
		}

		if (name.equals("main")) {
			if (returnType != VoidType.VOID) {
				throw new TypeMismatchError("Main must return void", ctx);
			}
			if (!arguments.isEmpty()) {
				throw new TypeMismatchError("Main must not take any arguments", ctx);
			}
		}

		return new MethodDescriptor(name, returnType, arguments, body);
	}

	public void loadBody(DecafSemanticChecker checker, Method_declContext ctx) {
		checker.pushMethodDescriptor(this);
		body.loadBlock(checker, ctx.block());
		checker.popMethodDescriptor();
	}

	@Override
	public void prettyPrint(PrintWriter pw, String prefix) {
		pw.println(prefix + getClass().getSimpleName());
		pw.print(prefix + "-returnType: ");
		getType().prettyPrint(pw,"");
		pw.println("\n" + prefix + "-name: " + getName());
		pw.print(prefix + "-parameters:\n");
		for(VariableDescriptor var : arguments ){
			var.prettyPrint(pw, prefix + "    ");
		}
		pw.println();
		body.prettyPrint(pw, prefix + "    ");
		pw.println(prefix + "end " + getName() + " " + getClass().getSimpleName());
		pw.println();
	}

	@Override
	public CFG generateCFG(CFGContext context) {
		BasicBlock methodBlock = BasicBlock.create(this);
		CFG bodyCFG = body.generateCFG(context);
		// BasicBlock returnBlock = BasicBlock.create(ReturnStmt.create());
		methodBlock.setNextBlock(bodyCFG.getEntryBlock());
		bodyCFG.addPreviousBlock(methodBlock);
		// bodyCFG.addNextBlock(returnBlock);
		// returnBlock.setPreviousBlock(bodyCFG.getExitBlock());
		return new CFG(methodBlock, bodyCFG.getExitBlock());

	}

	@Override
	public void cfgPrint(PrintWriter pw, String prefix) {
		pw.println(getType() + " " + getName());
	}

	@Override
	public void generateAssembly(AssemblyContext ctx) {
		int PARAMS_IN_REGS = 6;
		for(int i = 0; i < arguments.size(); i++) {
			VariableDescriptor node = arguments.get(i);
			long descriptorLength = node.getType().getLength();
			for(int j =0; j < descriptorLength; j++) {
				switch(i) {
				case 0:
					node.storeStack(j, Register.create("%rdi"), ctx);
					break;
				case 1:
					node.storeStack(j, Register.create("%rsi"), ctx);
					break;
				case 2:
					node.storeStack(j, Register.create("%rdx"), ctx);
					break;
				case 3:
					node.storeStack(j, Register.create("%rcx"), ctx);
					break;
				case 4:
					node.storeStack(j, Register.create("%r8"), ctx);
					break;
				case 5:
					node.storeStack(j, Register.create("%r9"), ctx);
					break;
				default:
					//starting position at 2 because first param is at +16(%rbp)
					node.setStackPosition(j, PARAMS_IN_REGS - 2 - i, ctx);
				}	
			}
		}
    }

	@Override
	public long getNumStackAllocations() {
		return Math.min(arguments.size(), 6);
	}
	
	@Override
	public Set<Descriptor> getConsumedDescriptors() {
		return new HashSet<Descriptor>(arguments);
	}

	@Override
	public Set<Descriptor> getGeneratedDescriptors() {
		return Collections.emptySet();
	}

	@Override
	public List<Optimizable> generateTemporaries(OptimizerContext context, boolean skipGeneration) {
		context.getExprToTemp().clear();
		context.getExprToVal().clear();
		context.getVarToVal().clear();
		return Collections.singletonList(this);
	}
}
